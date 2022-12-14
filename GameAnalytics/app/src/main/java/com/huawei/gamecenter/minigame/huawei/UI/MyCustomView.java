/**
 * Copyright 2022. Huawei Technologies Co., Ltd. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huawei.gamecenter.minigame.huawei.UI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import com.huawei.gamecenter.minigame.huawei.GameAnalyticsMgr;
import com.huawei.gamecenter.minigame.huawei.GameControler;
import com.huawei.gamecenter.minigame.huawei.R;
import com.huawei.gamecenter.minigame.huawei.Until.Constant;
import com.huawei.gamecenter.minigame.huawei.Until.HMSLogHelper;
import com.huawei.gamecenter.minigame.huawei.model.Artillery;
import com.huawei.gamecenter.minigame.huawei.model.Bullet;
import com.huawei.gamecenter.minigame.huawei.model.Enemy;
import com.huawei.gamecenter.minigame.huawei.model.MyPoint;
import com.huawei.gamecenter.minigame.huawei.model.MyRect;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

import androidx.annotation.Nullable;

public class MyCustomView extends View implements GameControler {
    private static final String TAG = "MyCustomView";
    public static Boolean isRefresh = true;
    private final int screenWidth;
    private final int cordon;
    private final int screenHeight;
    private boolean isSpawning = false;
    private float moveSpd = (float) 4;

    private Bitmap bmScaled;
    private Bullet bullet;
    private Artillery artilleryObj;
    private Enemy enemy;
    private Bitmap bmPaoDan;
    private Bitmap bmDaoDan;
    private Bitmap bmBoom;

    private final List<MyPoint> bulletPoints = new ArrayList<>();
    private final List<MyPoint> enemyPoints = new ArrayList<>();
    private final Random positionRand = new Random();
    private final Runnable spawnRunnable = this::instantiateEnemy;
    private MyCustomView.BeatEnemyListener beatEnemyListener;

    public MyCustomView(@Nullable Context context) {
        this(context, null);
    }

    public MyCustomView(@Nullable Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        DisplayMetrics dm;
        dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        cordon = (int) (screenHeight * 0.75);
        init();
    }

    @SuppressWarnings("IntegerDivisionInFloatingPointContext")
    private void init() {
        Bitmap bmSrc = BitmapFactory.decodeResource(getResources(), R.mipmap.paotai);
        Matrix matrix = new Matrix();
        matrix.setScale(0.6f, 0.6f);
        bmScaled = Bitmap.createBitmap(bmSrc, 0, 0, bmSrc.getWidth(), bmSrc.getHeight(), matrix, true);
        artilleryObj = new Artillery(new Matrix(), new Paint(), bmScaled);
        artilleryObj.setCenter(screenWidth / 2, screenHeight - artilleryObj.getBitmap().getHeight() / 5);
        artilleryObj.getMatrix().postTranslate(artilleryObj.getCenterX() - artilleryObj.getBitmap().getWidth() / 2, artilleryObj.getCenterY() - (int) (artilleryObj.getBitmap().getHeight() * 0.7));
        bmDaoDan = BitmapFactory.decodeResource(getResources(), R.mipmap.daodang);
        bmDaoDan = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.daodang),
                0, 0, bmDaoDan.getWidth(), bmDaoDan.getHeight(), matrix, true);
        Matrix matrixBoom = new Matrix();
        matrix.setScale(0.9f, 0.9f);
        bmBoom = BitmapFactory.decodeResource(getResources(), R.mipmap.boom);
        bmBoom = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.boom),
                0, 0, bmBoom.getWidth(), bmBoom.getHeight(), matrixBoom, true);

        Paint enemyPaint = new Paint();
        enemy = new Enemy(enemyPaint, moveSpd, bmDaoDan.getWidth() / 2, bmDaoDan);

        Paint bulletPaint = new Paint();
        Matrix matrixPd = new Matrix();
        matrixPd.setScale(0.65f, 0.65f);
        bmPaoDan = BitmapFactory.decodeResource(getResources(), R.mipmap.paodan);
        bmPaoDan = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.paodan),
                0, 0, bmPaoDan.getWidth(), bmPaoDan.getHeight(), matrixPd, true);
        bullet = new Bullet(bulletPaint, bmPaoDan.getWidth() / 2, 6, bmPaoDan);
    }

    /**
     * @param canvas UI???????????????????????????????????????????????????
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawShot(canvas);
    }

    @SuppressWarnings({"IntegerDivisionInFloatingPointContext", "UnusedAssignment"})
    private void drawShot(Canvas canvas) {
        Paint pLine = new Paint();
        pLine.setColor(Color.RED);
        canvas.drawLine(0, cordon, screenWidth, cordon, pLine);
        artilleryObj.getMatrix().reset();
        artilleryObj.getMatrix().postTranslate(artilleryObj.getCenterX() - artilleryObj.getBitmap().getWidth() / 2, artilleryObj.getCenterY() - (int) (artilleryObj.getBitmap().getHeight() * 0.7));
        canvas.drawBitmap(artilleryObj.getBitmap(), artilleryObj.getMatrix(), artilleryObj.getPaint());
        for (int i = 0; i < enemyPoints.size(); i++) {
            if (enemyPoints.get(i).getY() + bmBoom.getHeight() / 2 >= cordon) {
                // ?????????????????????????????????
                canvas.drawBitmap(bmBoom, enemyPoints.get(i).getX(), enemyPoints.get(i).getY(), enemy.getPaint());
                gmEnd();
                enemyPoints.remove(i--);
                break;
            }
            if (enemyPoints.get(i).isOutOfBoundsWithOutTop(screenWidth, screenHeight - bmScaled.getHeight())) {
                enemyPoints.remove(i--);
            } else {
                canvas.drawBitmap(enemy.getBitmap(), enemyPoints.get(i).getX(), enemyPoints.get(i).getY(), enemy.getPaint());
                if (!isRefresh) {
                    enemyPoints.get(i).move(enemy.getMoveStep(), true);
                }
            }
        }

        for (int i = 0; i < bulletPoints.size(); i++) {
            if (bulletPoints.get(i).isOutOfBounds(screenWidth, screenHeight)) {
                bulletPoints.remove(i--);
            } else {
                // ??????????????????
                canvas.drawBitmap(bullet.getBitmap(), bulletPoints.get(i).getX() - bullet.getBitmap().getWidth() / 2, bulletPoints.get(i).getY() - (int) (artilleryObj.getBitmap().getHeight() * 0.9), bullet.getPaint());
                if (!isRefresh) {
                    bulletPoints.get(i).move(bullet.getMoveStep() + 2, false);
                }
                // ??????????????????
                for (int j = 0; j < enemyPoints.size(); j++) {
                    MyRect r = new MyRect();
                    r.setBounds(bulletPoints.get(i).getX() - bullet.getBitmap().getWidth() / 2, bulletPoints.get(i).getY() - (int) (artilleryObj.getBitmap().getHeight() * 0.9), (int) (bmPaoDan.getWidth() * 0.4), (int) (bmPaoDan.getHeight() * 0.7));
                    MyRect p = new MyRect();
                    p.setBounds(enemyPoints.get(j).getX(), enemyPoints.get(j).getY(), (int) (bmDaoDan.getWidth() * 0.5), (int) (bmDaoDan.getHeight() * 0.6));
                    if (r.allIntersects(p)) {
                        canvas.drawBitmap(bmBoom, enemyPoints.get(j).getX(), enemyPoints.get(j).getY(), enemy.getPaint());
                        // ????????????
                        bulletPoints.remove(i--);
                        // ????????????
                        enemyPoints.remove(j);
                        // ??????????????????
                        if (beatEnemyListener != null)
                            beatEnemyListener.onBeatEnemy(0);
                        break;
                    }

                }
            }

        }

        // ?????????????????????????????????????????????????????????????????????????????????,?????????????????????
        // ?????????????????????
        int maxEnemyNum = 6;
        if (enemyPoints.size() < maxEnemyNum && !isSpawning && !isRefresh) {
            isSpawning = true;
            postDelayed(spawnRunnable, 1000);
        }

        postInvalidateDelayed(10);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // ?????????????????? ????????????????????????????????????????????? ???????????????,???????????????????????????
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // ??????????????????
            float currentRotate;
            if ((event.getY() > artilleryObj.getCenterY()) & (event.getX() > artilleryObj.getCenterX()) || (event.getY() > artilleryObj.getCenterY()) & (artilleryObj.getCenterX() > event.getX())) {
                currentRotate = (float) Math.toDegrees(Math.atan(-(artilleryObj.getCenterY() - event.getY()) / (event.getX() - artilleryObj.getCenterX())));
            } else if (event.getY() == artilleryObj.getCenterY() || event.getX() == artilleryObj.getCenterX()) {
                currentRotate = (float) Math.toDegrees(Math.atan(0));
            } else {
                currentRotate = (float) Math.toDegrees(Math.atan((artilleryObj.getCenterY() - event.getY()) / (event.getX() - artilleryObj.getCenterX())));
            }

            // ???????????????????????????????????????,????????????????????????bug??????
            if (!isRefresh) {
                bulletPoints.add(new MyPoint(artilleryObj.getCenterX(), artilleryObj.getCenterY(), Math.toRadians(currentRotate)));
                beatEnemyListener.onFire();
            }
        }
        return true;
    }

    // ???????????? instantiateEnemy
    private void instantiateEnemy() {
        if (!isRefresh) {
            enemyPoints.add(new MyPoint(positionRand.nextInt(screenWidth - enemy.getBitmap().getWidth()) + enemy.getBitmap().getWidth() / 2, -50, Math.toRadians(-90)));
        }
        isSpawning = false;
    }

    public void setBeatEnemyListener(BeatEnemyListener listener) {
        this.beatEnemyListener = listener;
    }

    /**
     * ?????????????????????????????????????????????????????????????????????
     */
    @Override
    public void gameSwitch(Boolean refresh) {
        isRefresh = refresh;
        if (!isRefresh) {
            //????????????????????????
            String eventId = "gameStart";
            LinkedHashMap<String, String> paramMap = new LinkedHashMap<>();
            paramMap.put("timeStamp", String.valueOf(System.currentTimeMillis()));
            GameAnalyticsMgr.reportOnStreamEvent(eventId, paramMap);
        } else {
            //????????????????????????
            String eventId = "gamePause";
            LinkedHashMap<String, String> paramMap = new LinkedHashMap<>();
            paramMap.put("timeStamp", String.valueOf(System.currentTimeMillis()));
            GameAnalyticsMgr.reportOnStreamEvent(eventId, paramMap);
        }
    }

    @Override
    public void setGameDifficulty(int gameLevel) {
        HMSLogHelper.getSingletonInstance().debug(TAG, "Game difficult level is:" + gameLevel);
    }

    @Override
    public void setAbscissa(int dpX) {
        // ?????????????????????????????????
        artilleryObj.setCenterX(artilleryObj.getCenterX() + dpX);
    }

    @Override
    public void setEmSpd(int spd, int level) {
        bulletPoints.clear();
        enemyPoints.clear();
        if (spd == Constant.MODE_THREE) {
            moveSpd = (float) (moveSpd + 0.2 * level);
            enemy.setMoveStep(moveSpd);
        }
        if (spd == Constant.M0DE_TWO) {
            HMSLogHelper.getSingletonInstance().debug(TAG, "moveSpd : " + moveSpd);
        }
    }

    private void gmEnd() {
        isRefresh = true;
        if (beatEnemyListener != null)
            beatEnemyListener.gameEnd(Constant.MODE_ONE);
    }

    // ??????????????????????????????
    public interface BeatEnemyListener {
        /**
         * @param showMode ?????????????????????????????????????????????????????????????????????????????????????????????
         */
        void onBeatEnemy(int showMode);

        void onFire();

        void gameEnd(int i);
    }
}
