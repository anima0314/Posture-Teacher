package com.gnupr.postureteacher;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.gnupr.postureteacher.Databases.EntityClass.Measure2DatasEntity;
import com.gnupr.postureteacher.Databases.EntityClass.Measure2RoundsEntity;
import com.gnupr.postureteacher.Databases.MeasureRoomDatabase;
import com.google.mediapipe.components.CameraHelper;
import com.google.mediapipe.components.CameraXPreviewHelper;
import com.google.mediapipe.components.ExternalTextureConverter;
import com.google.mediapipe.components.FrameProcessor;
import com.google.mediapipe.components.PermissionHelper;
import com.google.mediapipe.formats.proto.LandmarkProto.NormalizedLandmark;
import com.google.mediapipe.formats.proto.LandmarkProto.NormalizedLandmarkList;
import com.google.mediapipe.framework.AndroidAssetUtil;
import com.google.mediapipe.framework.AndroidPacketCreator;
import com.google.mediapipe.framework.Packet;
import com.google.mediapipe.framework.PacketGetter;
import com.google.mediapipe.glutil.EglManager;
import com.google.protobuf.InvalidProtocolBufferException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class PlankActivity extends AppCompatActivity {
    private static final String TAG = "PlankActivity";
    private static final String BINARY_GRAPH_NAME = "pose_tracking_gpu.binarypb";
    private static final String INPUT_VIDEO_STREAM_NAME = "input_video";
    private static final String OUTPUT_VIDEO_STREAM_NAME = "output_video";
    private static final String OUTPUT_LANDMARKS_STREAM_NAME = "pose_landmarks";
    private static final int NUM_FACES = 1;
    private static final CameraHelper.CameraFacing CAMERA_FACING = CameraHelper.CameraFacing.FRONT;
    private static final boolean FLIP_FRAMES_VERTICALLY = true;

    static {
        System.loadLibrary("mediapipe_jni");
        System.loadLibrary("opencv_java3");
    }

    private SurfaceTexture previewFrameTexture;
    private SurfaceView previewDisplayView;
    private EglManager eglManager;
    private FrameProcessor processor;
    private ExternalTextureConverter converter;
    private ApplicationInfo applicationInfo;
    private CameraXPreviewHelper cameraHelper;


    Handler ui_Handler = null;
    //UI 스레드 용 핸들러
    boolean ui_HandlerCheck = true;
    //UI 스레드 체크용
    private boolean startThreadCheck = true;

    private boolean startDialogCheck = true;
    //타이머 다이얼로그 시작 확인


    private int timer_hour, timer_minute, timer_second;
    //글로벌 시간
    private String text_hour, text_minute, text_second;
    //텍스트 상의 시간
    private String nowTime;
    //지금 시간
    private int totalTime = 0;
    //전체 시간
    private int globalTime = 0;
    //시작 이후의 시간
    private int spareTime = 100;
    //감지 예비 시간
    private int spareTimeMinus = 1;
    //예비 시간 빼는 값
    private boolean spareTimeCheck = false;
    //예비 시간 측정해도 되는지
    private int tempTime = 0;
    //예비 시간의 임시 시간(정상화 최종 측정)

    LocalDateTime timeMeasure2DataStart = LocalDateTime.now();
    LocalDateTime timeMeasure2DataEnd = LocalDateTime.now();
    //현재 전체 측정 시간
    private boolean timeDataCheck = true;
    //측정 시간 측정해도 되는지

    LocalDateTime timeMeasure2RoundStart = LocalDateTime.now();
    LocalDateTime timeMeasure2RoundEnd = LocalDateTime.now();
    //현재 상세 시간
    private boolean timeRoundCheck = true;
    //상세 시간 측정해도 되는지


    private String[] divideTime;
    //문자열에서 분할된 시간
    private Timer timer = new Timer();
    private boolean pauseTimerCheck = false;
    //false = 흘러감, true = 멈춤

    LocalDate nowLocalDate = LocalDate.now();
    LocalTime nowLocalTime = LocalTime.now();
    String formatedNowLocalTime = nowLocalDate.format(DateTimeFormatter.ofPattern("yyMMdd")) + nowLocalTime.format(DateTimeFormatter.ofPattern("HHmmss"));
    //날짜, 시간 & 문자열에 맞게 날짜+시간 변환

    LocalDateTime measure2RoundStart = LocalDateTime.now();
    LocalDateTime measure2RoundEnd = LocalDateTime.now();
    //현재 측정 시간

    LocalDateTime measure2DataStart = LocalDateTime.now();
    LocalDateTime measure2DataEnd = LocalDateTime.now();
    //현재 상세 시간

    private String UseTimerTimeDB = "01:22:33";
    //템플릿 타이머 시간 (시간:분:초)
    private final long finishtimeed = 2500;
    private long presstime = 0;

    private int plankTargetCount = 0;
    //플랭크할 횟수
    private int plankCurrentCount = 0;
    //현재 플랭크 횟수

    private int finalStopCheck = 0;
    //완전 종료 대기 확인
    //0 아무것도 아님, 1 쉬는 시간, 2 돌입 대기, 3 돌입

    //private TextView tv2;
    //private TextView tv6;
    private TextView tv_TimeCounter;

    private ImageView iv1;
    private ImageView iv2;
    //private ImageView iv3;
    private ImageView iv4;
    private ImageView iv5;
    //private ImageView iv6;

    class markPoint {
        float x;
        float y;
        float z;
    }

    private NormalizedLandmark[] bodyAdvancePoint = new NormalizedLandmark[33];
    //임시 랜드마크 포인트 변수
    private markPoint[] bodyMarkPoint = new markPoint[35];
    //몸 랜드마크 포인트 변수
    private float[] bodyRatioMeasurement = new float[33];
    //비율 계산값 변수(정규화 값)
    private boolean[][][] markResult = new boolean[33][33][33];
    //검사 결과 true/false 변수
    private boolean[] sideTotalResult = new boolean[2];
    //0=왼쪽, 1=오른쪽
    private boolean[] OutOfRangeSave = new boolean[33];
    //범위 벗어남 감지 저장 변수
    private float[][] resultAngleSave = new float[2][6];
    //부위 사라짐 감지용 0.5초 딜레이 저장 변수
    private int[] resultPosture = new int[4];
    //부위 별 최종 결과 0=미감지, 1=실패, 2=정상


    private float ratioPoint_1a, ratioPoint_1b, ratioPoint_2a, ratioPoint_2b;
    //비율 계산에 쓰일 포인트 변수 (왼쪽, 오른쪽)


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewLayoutResId());
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //tv2 = findViewById(R.id.tv2);
        //tv6 = findViewById(R.id.tv6);
        getTimeIntent();
        iv1= findViewById(R.id.imageView3);
        iv2= findViewById(R.id.imageView4);
        //iv3= findViewById(R.id.imageView5);
        iv4= findViewById(R.id.imageView6);
        iv5= findViewById(R.id.imageView7);
        //iv6= findViewById(R.id.imageView8);

        //tv.setText("000");
        if (startDialogCheck) {
            startDialog();
            startDialogCheck = false;
        }
        try {
            applicationInfo =
                    getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
        } catch (NameNotFoundException e) {
            Log.e(TAG, "Cannot find application info: " + e);
        }

        //tv.setText("111");
        previewDisplayView = new SurfaceView(this);
        setupPreviewDisplayView();
        //tv.setText("222");

        AndroidAssetUtil.initializeNativeAssetManager(this);
        eglManager = new EglManager(null);
        //tv.setText("333");
        processor =
                new FrameProcessor(
                        this,
                        eglManager.getNativeContext(),
                        BINARY_GRAPH_NAME,
                        INPUT_VIDEO_STREAM_NAME,
                        OUTPUT_VIDEO_STREAM_NAME);
        processor
                .getVideoSurfaceOutput()
                .setFlipY(FLIP_FRAMES_VERTICALLY);

        //tv.setText("444");
        PermissionHelper.checkAndRequestCameraPermissions(this);
        //tv.setText("555");
        AndroidPacketCreator packetCreator = processor.getPacketCreator();
        //tv.setText("666");
        Map<String, Packet> inputSidePackets = new HashMap<>();
        //tv.setText("888");
        processor.setInputSidePackets(inputSidePackets);
        //tv.setText("999");

        ui_Handler = new Handler();
        ThreadClass callThread = new ThreadClass();

        if (Log.isLoggable(TAG, Log.WARN)) {
            processor.addPacketCallback(
                    OUTPUT_LANDMARKS_STREAM_NAME,
                    (packet) -> {
                        byte[] landmarksRaw = PacketGetter.getProtoBytes(packet);
                        try {
                            NormalizedLandmarkList poseLandmarks = NormalizedLandmarkList.parseFrom(landmarksRaw);
                            //tv6.setText("a");
                            ratioPoint_1a = poseLandmarks.getLandmark(11).getY() * 1000f;
                            ratioPoint_1b = poseLandmarks.getLandmark(13).getY() * 1000f;
                            ratioPoint_2a = poseLandmarks.getLandmark(12).getY() * 1000f;
                            ratioPoint_2b = poseLandmarks.getLandmark(14).getY() * 1000f;
                            //tv6.setText("b");
                            for (int i = 0; i <= 32; i++) {
                                bodyMarkPoint[i] = new markPoint();
                                //tv6.setText("c");
                                bodyAdvancePoint[i] = poseLandmarks.getLandmark(i);
                                //tv6.setText("d");
                                bodyMarkPoint[i].x = bodyAdvancePoint[i].getY() * 1000f; //사실은 y축을 x축이라 속이는 것
                                //tv6.setText("e");
                                bodyMarkPoint[i].y = bodyAdvancePoint[i].getX() * 1000f; //사실은 x축을 y축이라 속이는 것
                                //tv6.setText("f");
                                bodyMarkPoint[i].z = bodyAdvancePoint[i].getZ() * 1000f;
                                //tv6.setText("g");
                                bodyRatioMeasurement[i] = bodyMarkPoint[i].x / (ratioPoint_1b - ratioPoint_1a);
                                //tv6.setText("h");
                                bodyRatioMeasurement[i] = bodyMarkPoint[i].y / (ratioPoint_1b - ratioPoint_1a);
                                //tv6.setText("i");
                                bodyRatioMeasurement[i] = bodyMarkPoint[i].z / (ratioPoint_1b - ratioPoint_1a);
                                //tv6.setText("k");
                                if ((-100f <= bodyMarkPoint[i].x && bodyMarkPoint[i].x <= 1100f) && (-100f <= bodyMarkPoint[i].y && bodyMarkPoint[i].y <= 1100f))
                                    OutOfRangeSave[i] = true;
                                else
                                    OutOfRangeSave[i] = false;
                            }
                            //tv.setText("X:" + bodyMarkPoint[25].x + " / Y:" + bodyMarkPoint[25].y + " / Z:" + bodyMarkPoint[25].z + "\n/ANGLE:" + getLandmarksAngleTwo(bodyMarkPoint[23], bodyMarkPoint[25], bodyMarkPoint[27], 'x', 'y'));

                            if (startThreadCheck) {
                                ui_Handler.post(callThread);
                                // 핸들러를 통해 안드로이드 OS에게 작업을 요청
                                startThreadCheck = false;
                            }
                        } catch (InvalidProtocolBufferException e) {
                            Log.e(TAG, "Couldn't Exception received - " + e);
                            return;
                        }
                    }
            );
        }
    }

    class ThreadClass extends Thread {
        //갱신 UI 관리는 여기서
        @Override
        public void run() {
            //정상판별
            if (sideTotalResult[1] && sideTotalResult[0]) {
                //tv6.setText("1");
                //tv2.setText("현 자세 정상입니다.");
            } else if (sideTotalResult[1]) {
                //tv6.setText("2");
                //tv2.setText("오른쪽 자세 정상입니다.");
            } else if (sideTotalResult[0]) {
                //tv6.setText("3");
                //tv2.setText("왼쪽 자세 정상입니다.");
            } else {
                //tv6.setText("4");
                //tv2.setText("현 자세 비정상입니다.");
            }

            if (bodyMarkPoint[11].z > bodyMarkPoint[12].z)
                getLandmarksAngleResult(0);
                //왼쪽
            else
                getLandmarksAngleResult(1);
            //오른쪽


            if(20 <= globalTime) {
                if (!pauseTimerCheck) {
                    if (getResultPosture(resultPosture) == 2) {
                        if (spareTime >= 90) {
                            if (spareTimeCheck && finalStopCheck == 0) {
                                if (tempTime >= 6) {
                                    Toast.makeText(getApplicationContext(), "자세가 정상입니다.", Toast.LENGTH_SHORT).show();
                                    saveMeasure2Datas();
                                    spareTimeCheck = false;
                                } else if (tempTime < 6)
                                    tempTime++;
                            }
                        }
                        spareTime = 100;
                    } else if (getResultPosture(resultPosture) == 1) {
                        if (spareTime <= 0) {
                            if (!spareTimeCheck) {
                                Toast.makeText(getApplicationContext(), "자세가 불안정한 상태입니다.", Toast.LENGTH_SHORT).show();
                                measure2DataStart = LocalDateTime.now();
                                spareTimeCheck = true;
                            }
                        }
                        if (spareTime > 0) {
                            spareTime -= spareTimeMinus;
                        }
                        if (tempTime > 0) {
                            tempTime = 0;
                        }
                    }
                }
                else if (pauseTimerCheck && finalStopCheck == 1) {
                    if(spareTime <= 0) {
                        saveMeasure2Datas();
                        spareTime = 100;
                        //쉬는 단계에 접어들 때 자세가 흐트러져 있을 경우 spareTime이 0 아래이면 그냥 저장을 해버림
                        //이미 시간 관련 정보는 다른 곳에서 저장되었기에 별 상관없음
                    }
                }
            }

            if(finalStopCheck == 0) {
                tv_TimeCounter.setText(nowTime);
            }
            else if(finalStopCheck == 1) {
                tv_TimeCounter.setText(timer_second + "초 간 쉬는 시간");
            }
            else if(finalStopCheck == 2 || finalStopCheck == 3) {
                tv_TimeCounter.setText(timer_second + "초 후 메인화면");
            }

            if(finalStopCheck == 2){
                if(20 <= globalTime) {
                    saveMeasure2Rounds();
                    if (spareTimeCheck) {
                        saveMeasure2Datas();
                    }
                }
            }

            if(finalStopCheck == 3 && timer_second <= 0) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                pauseTimerCheck = true;
                ui_HandlerCheck = false;
                finish();
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
                startThreadCheck = true;
            }
            if(ui_HandlerCheck) {
                ui_Handler.post(this);
            }
        }
    }



    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            // 반복실행할 구문
            globalTime++;
            if(!pauseTimerCheck) {
                // 0초 이상이면
                if (timer_second != 0) {
                    //1초씩 감소
                    timer_second--;

                    // 0분 이상이면
                } else if (timer_minute != 0) {
                    // 1분 = 60초
                    timer_second = 60;
                    timer_second--;
                    timer_minute--;

                    // 0시간 이상이면
                } else if (timer_hour != 0) {
                    // 1시간 = 60분
                    timer_second = 60;
                    timer_minute = 60;
                    timer_second--;
                    timer_minute--;
                    timer_hour--;
                }

                if(globalTime == 20) {
                    measure2RoundStart = LocalDateTime.now();
                }

                //시, 분, 초가 10이하(한자리수) 라면
                // 숫자 앞에 0을 붙인다 ( 8 -> 08 )
                if (timer_second <= 9) {
                    text_second = "0" + timer_second;
                } else {
                    text_second = Integer.toString(timer_second);
                }

                if (timer_minute <= 9) {
                    text_minute = "0" + timer_minute;
                } else {
                    text_minute = Integer.toString(timer_minute);
                }

                if (timer_hour <= 9) {
                    text_hour = "0" + timer_hour;
                } else {
                    text_hour = Integer.toString(timer_minute);
                }
                nowTime = text_hour + ":" + text_minute + ":" + text_second + "\n(" + spareTime + "% / " + tempTime + "pt)";
            }

            if (timer_hour == 0 && timer_minute == 0 && timer_second == 0) {
                /*timerTask.cancel();//타이머 종료
                timer.cancel();//타이머 종료
                timer.purge();//타이머 종료*/
                //중간에 잠시 멈추는 건 타이머를 죽이는 게 아니라 타이머를 보기로만 잠시 멈춰두고 다시 시작할 때 시간을 새로 갱신
                if(finalStopCheck == 0) {
                    timer_second += 20;
                    //플랭크 쉬는 시간 설정
                    finalStopCheck = 1;
                    pauseTimerCheck = true;
                }
                else if(finalStopCheck == 1) {
                    if (plankTargetCount == plankCurrentCount)
                    {
                        timer_second += 3;
                        finalStopCheck = 2;
                    }
                    else {
                        timer_hour = Integer.parseInt(divideTime[0]);
                        timer_minute = Integer.parseInt(divideTime[1]);
                        timer_second = Integer.parseInt(divideTime[2]);
                        plankCurrentCount++;
                        pauseTimerCheck = false;
                        finalStopCheck = 0;
                    }
                }
            }
        }
    };
    private void startDialog() {
        //이건 자르던지 바꾸던지 하셈
        tv_TimeCounter = findViewById(R.id.TimeCounter);
        AlertDialog.Builder msgBuilder = new AlertDialog.Builder(PlankActivity.this)
                .setTitle("시작 전 준비")
                .setMessage("하단의 확인 버튼을 누르고 나서 정확히 30초 뒤에 자세 측정을 본격적으로 시작됩니다. 그 이전에 종료시 데이터는 저장되지 않습니다. 그 동안 휴대폰을 적당한 위치에 배치해주시길 바랍니다.")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        divideTime = UseTimerTimeDB.split(":");
                        timer_hour = Integer.parseInt(divideTime[0]);
                        timer_minute = Integer.parseInt(divideTime[1]);
                        timer_second = Integer.parseInt(divideTime[2]);
                        totalTime = ((((timer_hour * 60) + timer_minute) * 60) + timer_second) * 1000;
                        timer.scheduleAtFixedRate(timerTask, 10000, 1000); //Timer 실행
                    }
                });
        AlertDialog msgDlg = msgBuilder.create();
        msgDlg.show();
    }

    public void onClickExit(View view) {
        if(20 <= globalTime) {
            if (finalStopCheck == 0) {
                saveMeasure2Rounds();
                if (spareTimeCheck) {
                    saveMeasure2Datas();
                }
            }
        }
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        pauseTimerCheck = true;
        ui_HandlerCheck = false;
        finish();
    }

    private void saveMeasure2Rounds() { //여기가 측정 시간 저장, 전체
        LocalDateTime Measure2RoundStartTime_num = measure2RoundStart;
        measure2RoundEnd = LocalDateTime.now();
        LocalDateTime Measure2RoundEndTime_num = measure2RoundEnd;
        int TargetCount = plankTargetCount;
        int CurrentCount = plankCurrentCount;

        Measure2RoundsEntity Measure2RoundsTable = new Measure2RoundsEntity();
        Measure2RoundsTable.setMeasure2RoundStartTime(Measure2RoundStartTime_num);
        Measure2RoundsTable.setMeasure2RoundEndTime(Measure2RoundEndTime_num);
        Measure2RoundsTable.setMeasure2RoundTargetCount(TargetCount);
        Measure2RoundsTable.setMeasure2RoundCurrentCount(CurrentCount);
        MeasureRoomDatabase.getDatabase(getApplicationContext()).getMeasure2RoundsDao().insert(Measure2RoundsTable);
        //MeasureRoomDatabase.getDatabase(getApplicationContext()).getMeasure2RoundsDao().deleteAll(); 이건 삭제

        Toast.makeText(this, "전체 시간 저장", Toast.LENGTH_SHORT).show();
        finalStopCheck = 3;
    }


    private void saveMeasure2Datas() { //여기가 감지 집중 시간 저장, 일시
        LocalDateTime Measure2DataStartTime_num = measure2DataStart;
        measure2DataEnd = LocalDateTime.now();
        LocalDateTime Measure2DataEndTime_num = measure2DataEnd;
        LocalDateTime Measure2RoundStartTimeFK_num = measure2RoundStart;

        Measure2DatasEntity Measure2DatasTable = new Measure2DatasEntity();
        Measure2DatasTable.setMeasure2DataStartTime(Measure2DataStartTime_num);
        Measure2DatasTable.setMeasure2DataEndTime(Measure2DataEndTime_num);
        Measure2DatasTable.setMeasure2RoundStartTimeFK(Measure2RoundStartTimeFK_num);
        MeasureRoomDatabase.getDatabase(getApplicationContext()).getMeasure2DatasDao().insert(Measure2DatasTable);
        //MeasureRoomDatabase.getDatabase(getApplicationContext()).getMeasure2DatasDao().deleteAll(); 이건 삭제

        Toast.makeText(this, "상세 시간 저장", Toast.LENGTH_SHORT).show();
    }

    public void angleCalculationResult(int firstPoint, int secondPoint, int thirdPoint, float oneAngle, float twoAngle) {
        if (getLandmarksAngleTwo(bodyMarkPoint[firstPoint], bodyMarkPoint[secondPoint], bodyMarkPoint[thirdPoint], 'x', 'y') >= oneAngle
                && getLandmarksAngleTwo(bodyMarkPoint[firstPoint], bodyMarkPoint[secondPoint], bodyMarkPoint[thirdPoint], 'x', 'y') <= twoAngle) {
            markResult[firstPoint][secondPoint][thirdPoint] = true;
        } else {
            markResult[firstPoint][secondPoint][thirdPoint] = false;
        }
    }

    public void getLandmarksAngleResult(int side) { //0=왼쪽, 1=오른쪽
        //첫번째 true if는 범위 내에 있을 때, 첫번째 false if는 범위 밖에 있을 때
        //두번째 true if는 검사 결과가 정상일 때, 두번째 false if는 검사 결과가 비정상일 때
        if (OutOfRangeSave[11 + side] == true && OutOfRangeSave[13 + side] == true && OutOfRangeSave[15 + side] == true) { //범위 판별
            angleCalculationResult(11 + side, 13 + side, 15 + side, 60f, 120f); //90f 120f | 70f 140f | 80f 130f
            //어깨-팔꿈치-팔목 팔각도
            if (markResult[11 + side][13 + side][15 + side] == true) { //각도 판별
                iv1.setImageResource(R.drawable.plank_arm_green);
                resultPosture[0] = 2;
            } else {
                iv1.setImageResource(R.drawable.plank_arm_red);
                resultPosture[0] = 1;
            }
        } else {
            //여기에 비감지(회색)
            iv1.setImageResource(R.drawable.plank_arm_gray);
            markResult[11 + side][13 + side][15 + side] = true;
            resultPosture[0] = 0;
        }

        if (OutOfRangeSave[7 + side] == true && OutOfRangeSave[11 + side] == true && OutOfRangeSave[23 + side] == true) { //범위 판별
            angleCalculationResult(7 + side, 11 + side, 23 + side, 150f, 210f); //130f 180f | 120f 180f | 140f 180f
            //엉덩이-허리-귀 척추각도
            if (markResult[7 + side][11 + side][23 + side] == true) { //각도 판별
                iv2.setImageResource(R.drawable.plank_spine_green);
                resultPosture[1] = 2;
            } else {
                iv2.setImageResource(R.drawable.plank_spine_red);
                resultPosture[1] = 1;
            }
        } else {
            //여기에 비감지(회색)
            iv2.setImageResource(R.drawable.plank_spine_gray);
            markResult[7 + side][11 + side][23 + side] = true;
            resultPosture[1] = 0;
        }
        /*
        if (OutOfRangeSave[11 + side] == true && OutOfRangeSave[13 + side] == true && OutOfRangeSave[15 + side] == true) { //범위 판별
            angleCalculationResult(11 + side, 13 + side, 15 + side, 80f, 130f); //140f 180f | 120f 180f X //90f 120f
            //엉덩이-팔꿈치-귀
            if (markResult[11 + side][13 + side][15 + side] == true) { //각도 판별
                iv3.setImageResource(R.drawable.elbow_green);
            } else {
                iv3.setImageResource(R.drawable.elbow_red);
            }
        } else {
            //여기에 비감지(회색)
            iv3.setImageResource(R.drawable.elbow_gray);
            markResult[11 + side][13 + side][15 + side] = true;
        }*/

        bodyMarkPoint[33 + side] = new markPoint();
        if(side == 0)
            bodyMarkPoint[33 + side].x = bodyAdvancePoint[7].getX() * 1000f + 300;
        else
            bodyMarkPoint[33 + side].x = bodyAdvancePoint[7].getX() * 1000f - 300;
        bodyMarkPoint[33 + side].y = bodyAdvancePoint[7].getY() * 1000f - 10;
        bodyMarkPoint[33 + side].z = bodyAdvancePoint[7].getZ() * 1000f + 10;
        if (OutOfRangeSave[7 + side] == true && OutOfRangeSave[11 + side] == true) { //범위 판별
            if (!Double.isNaN(getLandmarksAngleTwo(bodyMarkPoint[33 + side], bodyMarkPoint[7 + side], bodyMarkPoint[11 + side], 'x', 'y'))) {
                if (getLandmarksAngleTwo(bodyMarkPoint[33 + side], bodyMarkPoint[7 + side], bodyMarkPoint[11 + side], 'x', 'y') >= 60f
                        && getLandmarksAngleTwo(bodyMarkPoint[33 + side], bodyMarkPoint[7 + side], bodyMarkPoint[11 + side], 'x', 'y') <= 140f)
                { //90f 140f | 80f 160f | 80f 120f | 80f 140f
                    markResult[7 + side][7 + side][11 + side] = true;
                } else {
                    markResult[7 + side][7 + side][11 + side] = false;
                }
                if (markResult[7 + side][7 + side][11 + side] == true) { //각도 판별
                    iv4.setImageResource(R.drawable.plank_head_green);
                    resultPosture[2] = 2;
                } else {
                    iv4.setImageResource(R.drawable.plank_head_red);
                    resultPosture[2] = 1;
                }
            }
            //어깨-귀-귀너머 머리각도(x+300)
        } else {
            //여기에 비감지(회색)
            iv4.setImageResource(R.drawable.plank_head_gray);
            markResult[7 + side][7 + side][11 + side] = true;
            resultPosture[2] = 0;
        }

        if (OutOfRangeSave[23 + side] == true && OutOfRangeSave[25 + side] == true && OutOfRangeSave[27 + side] == true) { //범위 판별
            angleCalculationResult(23 + side, 25 + side, 27 + side, 140f, 210f); //90f 120f | 70f 140f
            //발목-무릎-엉덩이 무릎각도
            if (markResult[23 + side][25 + side][27 + side] == true) { //각도 판별
                iv5.setImageResource(R.drawable.plank_leg_green);
                resultPosture[3] = 2;
            } else {
                iv5.setImageResource(R.drawable.plank_leg_red);
                resultPosture[3] = 1;
            }
        } else {
            //여기에 비감지(회색)
            iv5.setImageResource(R.drawable.plank_leg_gray);
            markResult[23 + side][25 + side][27 + side] = true;
            resultPosture[3] = 0;
        }
/*
        if (OutOfRangeSave[25 + side] == true && OutOfRangeSave[29 + side] == true && OutOfRangeSave[31 + side] == true) { //범위 판별
            angleCalculationResult(25 + side, 29 + side, 31 + side, 90f, 130f); //100f 120f | 80f 140f
            //무릎-뒷꿈치-발 발목각도
            if (markResult[25 + side][29 + side][31 + side] == true) { //각도 판별
                iv6.setImageResource(R.drawable.ankle_green);
            } else {
                iv6.setImageResource(R.drawable.ankle_red);
            }
        } else {
            //여기에 비감지(회색)
            iv6.setImageResource(R.drawable.ankle_gray);
            markResult[25 + side][29 + side][31 + side] = true;
        }*/

        if (markResult[11 + side][13 + side][15 + side] && markResult[7 + side][11 + side][23 + side] && markResult[11 + side][13 + side][15 + side]
                && markResult[7 + side][7 + side][11 + side] && markResult[23 + side][25 + side][27 + side]) // && markResult[25 + side][29 + side][31 + side] 일단 이건 제외
            sideTotalResult[side] = true;
        else
            sideTotalResult[side] = false;
    }

    public static float getLandmarksAngleTwo(markPoint p1, markPoint p2, markPoint p3, char a, char b) {
        float p1_2 = 0f, p2_3 = 0f, p3_1 = 0f;
        if (a == b) {
            return 0;
        } else if ((a == 'x' || b == 'x') && (a == 'y' || b == 'y')) {
            p1_2 = (float) Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
            p2_3 = (float) Math.sqrt(Math.pow(p2.x - p3.x, 2) + Math.pow(p2.y - p3.y, 2));
            p3_1 = (float) Math.sqrt(Math.pow(p3.x - p1.x, 2) + Math.pow(p3.y - p1.y, 2));
        } else if ((a == 'x' || b == 'x') && (a == 'z' || b == 'z')) {
            p1_2 = (float) Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.z - p2.z, 2));
            p2_3 = (float) Math.sqrt(Math.pow(p2.x - p3.x, 2) + Math.pow(p2.z - p3.z, 2));
            p3_1 = (float) Math.sqrt(Math.pow(p3.x - p1.x, 2) + Math.pow(p3.z - p1.z, 2));
        } else if ((a == 'y' || b == 'y') && (a == 'z' || b == 'z')) {
            p1_2 = (float) Math.sqrt(Math.pow(p1.y - p2.y, 2) + Math.pow(p1.z - p2.z, 2));
            p2_3 = (float) Math.sqrt(Math.pow(p2.y - p3.y, 2) + Math.pow(p2.z - p3.z, 2));
            p3_1 = (float) Math.sqrt(Math.pow(p3.y - p1.y, 2) + Math.pow(p3.z - p1.z, 2));
        }
        float radian = (float) Math.acos((p1_2 * p1_2 + p2_3 * p2_3 - p3_1 * p3_1) / (2 * p1_2 * p2_3));
        float degree = (float) (radian / Math.PI * 180);
        return degree;
    }

    public int getResultPosture(int[] rP) {
        int twoCount = 0, oneCount = 0, zeroCount = 0; //녹색, 적색, 회색
        for(int i = 0;i<4;i++) {
            if (rP[i] == 2) {
                twoCount++;
            }
            else if (rP[i] == 1) {
                oneCount++;
            }
            else {
                zeroCount++;
            }
        }

        if(zeroCount == 4) {
            spareTimeMinus = 0;
            return 0;
        }
        else if(zeroCount == 3) {
            if(oneCount == 0) {
                spareTimeMinus = 0;
                return 2;
            }
            else {
                spareTimeMinus = 4;
                return 1;
            }
        }
        else if(zeroCount == 2) {
            if(oneCount == 0) {
                spareTimeMinus = 0;
                return 2;
            }
            else if(oneCount == 1) {
                spareTimeMinus = 2;
                return 1;
            }
            else {
                spareTimeMinus = 4;
                return 1;
            }
        }
        else if(zeroCount == 1) {
            if(oneCount == 0) {
                spareTimeMinus = 0;
                return 2;
            }
            else if(oneCount == 1) {
                spareTimeMinus = 1;
                return 1;
            }
            else if(oneCount == 2) {
                spareTimeMinus = 2;
                return 1;
            }
            else {
                spareTimeMinus = 4;
                return 1;
            }
        }
        else {
            if(oneCount == 0) {
                spareTimeMinus = 0;
                return 2;
            }
            else if(oneCount == 1) {
                spareTimeMinus = 1;
                return 1;
            }
            else if(oneCount == 2) {
                spareTimeMinus = 2;
                return 1;
            }
            else if(oneCount == 3) {
                spareTimeMinus = 3;
                return 1;
            }
            else {
                spareTimeMinus = 4;
                return 1;
            }
        }
    }

    @Override
    public void onBackPressed() {
        long tempTimeOBP = System.currentTimeMillis();
        long intervalTime = tempTimeOBP - presstime;

        if (0 <= intervalTime && finishtimeed >= intervalTime)
        {
            if(1 <= globalTime) {
                if(20 <= globalTime) {
                    if (finalStopCheck == 0) {
                        saveMeasure2Rounds();
                        if (spareTimeCheck) {
                            saveMeasure2Datas();
                        }
                    }
                }
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                pauseTimerCheck = true;
                ui_HandlerCheck = false;
                finish();
            }
        }
        else
        {
            presstime = tempTimeOBP;
            Toast.makeText(getApplicationContext(), "한 번 더 누르면 뒤로 갑니다", Toast.LENGTH_SHORT).show();
        }
    }

    public void getTimeIntent() {
        Intent intent = getIntent();
        int intentHour = intent.getIntExtra("hour", 1);
        int intentMinute = intent.getIntExtra("minute", 0);
        UseTimerTimeDB = intentHour + ":" + intentMinute + ":20";
    }

    //pose
    protected int getContentViewLayoutResId() {
        return R.layout.activity_plank;
    }

    @Override
    protected void onResume() {
        super.onResume();
        converter =
                new ExternalTextureConverter(
                        eglManager.getContext(), 2);
        converter.setFlipY(FLIP_FRAMES_VERTICALLY);
        converter.setConsumer(processor);
        if (PermissionHelper.cameraPermissionsGranted(this)) {
            startCamera();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        converter.close();

        previewDisplayView.setVisibility(View.GONE);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    protected void onCameraStarted(SurfaceTexture surfaceTexture) {
        previewFrameTexture = surfaceTexture;
        previewDisplayView.setVisibility(View.VISIBLE);
    }

    protected Size cameraTargetResolution() {
        return null;
    }

    public void startCamera() {
        cameraHelper = new CameraXPreviewHelper();
        cameraHelper.setOnCameraStartedListener(
                surfaceTexture -> {
                    onCameraStarted(surfaceTexture);
                });
        CameraHelper.CameraFacing cameraFacing = CameraHelper.CameraFacing.FRONT;
        cameraHelper.startCamera(
                this, cameraFacing, previewFrameTexture, cameraTargetResolution());
    }

    protected Size computeViewSize(int width, int height) {
        return new Size(width, height);
    }

    protected void onPreviewDisplaySurfaceChanged(
            SurfaceHolder holder, int format, int width, int height) {
        Size viewSize = computeViewSize(width, height);
        Size displaySize = cameraHelper.computeDisplaySizeFromViewSize(viewSize);
        boolean isCameraRotated = cameraHelper.isCameraRotated();

        converter.setSurfaceTextureAndAttachToGLContext(
                previewFrameTexture,
                isCameraRotated ? displaySize.getHeight() : displaySize.getWidth(),
                isCameraRotated ? displaySize.getWidth() : displaySize.getHeight());
    }

    private void setupPreviewDisplayView() {
        previewDisplayView.setVisibility(View.GONE);
        ViewGroup viewGroup = findViewById(R.id.preview_display_layout);
        viewGroup.addView(previewDisplayView);

        previewDisplayView
                .getHolder()
                .addCallback(
                        new SurfaceHolder.Callback() {
                            @Override
                            public void surfaceCreated(SurfaceHolder holder) {
                                processor.getVideoSurfaceOutput().setSurface(holder.getSurface());
                            }

                            @Override
                            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                                onPreviewDisplaySurfaceChanged(holder, format, width, height);
                            }

                            @Override
                            public void surfaceDestroyed(SurfaceHolder holder) {
                                processor.getVideoSurfaceOutput().setSurface(null);
                            }
                        });
    }

    private static String getPoseLandmarksDebugString(NormalizedLandmarkList poseLandmarks) {
        String poseLandmarkStr = "Pose landmarks: " + poseLandmarks.getLandmarkCount() + "\n";
        int landmarkIndex = 0;
        for (NormalizedLandmark landmark : poseLandmarks.getLandmarkList()) {
            poseLandmarkStr +=
                    "\tLandmark ["
                            + landmarkIndex
                            + "]: ("
                            + landmark.getX()
                            + ", "
                            + landmark.getY()
                            + ", "
                            + landmark.getZ()
                            + ")\n";
            ++landmarkIndex;
        }
        return poseLandmarkStr;
    }
}