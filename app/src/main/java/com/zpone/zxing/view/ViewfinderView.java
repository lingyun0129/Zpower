/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zpone.zxing.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.google.zxing.ResultPoint;
import com.zpone.R;
import com.zpone.zxing.camera.CameraManager;

import java.util.Collection;
import java.util.HashSet;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder rectangle and partial
 * transparency outside it, as well as the laser scanner animation and result points.
 * �Զ����View������ʱ�м���ʾ��
 */
public final class ViewfinderView extends View {

  private static final int[] SCANNER_ALPHA = {0, 64, 128, 192, 255, 192, 128, 64};
  private static final long ANIMATION_DELAY = 100L;
  private static final int OPAQUE = 0xFF;

  private final Paint paint;
  private Bitmap resultBitmap;
  private final int maskColor;
  private final int resultColor;
  private final int frameColor;
  private final int laserColor;
  private final int resultPointColor;
  private final int statusColor; // 提示文字颜色
  private int scannerAlpha;
  private Collection<ResultPoint> possibleResultPoints;
  private Collection<ResultPoint> lastPossibleResultPoints;
  // 扫描线移动的y
  private int scanLineTop;
  // 扫描线移动速度
  private final int SCAN_VELOCITY = 15;
  // 扫描线
  Bitmap scanLight;
  // This constructor is used when the class is built from an XML resource.
  public ViewfinderView(Context context, AttributeSet attrs) {
    super(context, attrs);

    // Initialize these once for performance rather than calling them every time in onDraw().
    paint = new Paint();
    Resources resources = getResources();
    maskColor = resources.getColor(R.color.viewfinder_mask);
    resultColor = resources.getColor(R.color.result_view);
    frameColor = resources.getColor(R.color.viewfinder_frame);
    laserColor = resources.getColor(R.color.viewfinder_laser);
    resultPointColor = resources.getColor(R.color.possible_result_points);
    statusColor = resources.getColor(R.color.status_text);
    scannerAlpha = 0;
    possibleResultPoints = new HashSet<ResultPoint>(5);
    scanLight = BitmapFactory.decodeResource(resources,
            R.drawable.scan_light);
  }

  @Override
  public void onDraw(Canvas canvas) {
    Rect frame = CameraManager.get().getFramingRect();
    if (frame == null) {
      return;
    }
    int width = canvas.getWidth();
    int height = canvas.getHeight();

    // Draw the exterior (i.e. outside the framing rect) darkened
    paint.setColor(resultBitmap != null ? resultColor : maskColor);
    canvas.drawRect(0, 0, width, frame.top, paint);
    canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
    canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
    canvas.drawRect(0, frame.bottom + 1, width, height, paint);

    if (resultBitmap != null) {
      // Draw the opaque result bitmap over the scanning rectangle
      paint.setAlpha(OPAQUE);
      canvas.drawBitmap(resultBitmap, frame.left, frame.top, paint);
    } else {
      drawFrameBounds(canvas, frame);
      drawStatusText(canvas, frame, width);
      drawScanLight(canvas, frame);
      // Draw a two pixel solid black border inside the framing rect
      paint.setColor(frameColor);
      canvas.drawRect(frame.left, frame.top, frame.right + 1, frame.top + 2, paint);
      canvas.drawRect(frame.left, frame.top + 2, frame.left + 2, frame.bottom - 1, paint);
      canvas.drawRect(frame.right - 1, frame.top, frame.right + 1, frame.bottom - 1, paint);
      canvas.drawRect(frame.left, frame.bottom - 1, frame.right + 1, frame.bottom + 1, paint);

      // Draw a red "laser scanner" line through the middle to show decoding is active
      paint.setColor(laserColor);
      paint.setAlpha(SCANNER_ALPHA[scannerAlpha]);
      scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.length;
      int middle = frame.height() / 2 + frame.top;
      //canvas.drawRect(frame.left + 2, middle - 1, frame.right - 1, middle + 2, paint);

      Collection<ResultPoint> currentPossible = possibleResultPoints;
      Collection<ResultPoint> currentLast = lastPossibleResultPoints;
      if (currentPossible.isEmpty()) {
        lastPossibleResultPoints = null;
      } else {
        possibleResultPoints = new HashSet<ResultPoint>(5);
        lastPossibleResultPoints = currentPossible;
        paint.setAlpha(OPAQUE);
        paint.setColor(resultPointColor);
        for (ResultPoint point : currentPossible) {
          canvas.drawCircle(frame.left + point.getX(), frame.top + point.getY(), 6.0f, paint);
        }
      }
      if (currentLast != null) {
        paint.setAlpha(OPAQUE / 2);
        paint.setColor(resultPointColor);
        for (ResultPoint point : currentLast) {
          canvas.drawCircle(frame.left + point.getX(), frame.top + point.getY(), 3.0f, paint);
        }
      }

      // Request another update at the animation interval, but only repaint the laser line,
      // not the entire viewfinder mask.
      postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top, frame.right, frame.bottom);
    }
  }

  public void drawViewfinder() {
    resultBitmap = null;
    invalidate();
  }

  /**
   * Draw a bitmap with the result points highlighted instead of the live scanning display.
   *
   * @param barcode An image of the decoded barcode.
   */
  public void drawResultBitmap(Bitmap barcode) {
    resultBitmap = barcode;
    invalidate();
  }

  public void addPossibleResultPoint(ResultPoint point) {
    possibleResultPoints.add(point);
  }
  /**
   * 绘制取景框边框
   *
   * @param canvas
   * @param frame
   */
  private void drawFrameBounds(Canvas canvas, Rect frame) {

    paint.setColor(Color.WHITE);
    paint.setStrokeWidth(2);
    paint.setStyle(Paint.Style.STROKE);

    canvas.drawRect(frame, paint);

    paint.setColor(Color.BLUE);
    paint.setStyle(Paint.Style.FILL);

    int corWidth = 15;
    int corLength = 45;

    // 左上角
    canvas.drawRect(frame.left - corWidth, frame.top, frame.left, frame.top
            + corLength, paint);
    canvas.drawRect(frame.left - corWidth, frame.top - corWidth, frame.left
            + corLength, frame.top, paint);
    // 右上角
    canvas.drawRect(frame.right, frame.top, frame.right + corWidth,
            frame.top + corLength, paint);
    canvas.drawRect(frame.right - corLength, frame.top - corWidth,
            frame.right + corWidth, frame.top, paint);
    // 左下角
    canvas.drawRect(frame.left - corWidth, frame.bottom - corLength,
            frame.left, frame.bottom, paint);
    canvas.drawRect(frame.left - corWidth, frame.bottom, frame.left
            + corLength, frame.bottom + corWidth, paint);
    // 右下角
    canvas.drawRect(frame.right, frame.bottom - corLength, frame.right
            + corWidth, frame.bottom, paint);
    canvas.drawRect(frame.right - corLength, frame.bottom, frame.right
            + corWidth, frame.bottom + corWidth, paint);
  }

  /**
   * 绘制提示文字
   *
   * @param canvas
   * @param frame
   * @param width
   */
  private void drawStatusText(Canvas canvas, Rect frame, int width) {

    String statusText1 = getResources().getString(
            R.string.viewfinderview_status_text1);
    String statusText2 = getResources().getString(
            R.string.viewfinderview_status_text2);
    int statusTextSize = 45;
    int statusPaddingTop = 100;

    paint.setColor(statusColor);
    paint.setTextSize(statusTextSize);

    int textWidth1 = (int) paint.measureText(statusText1);
    canvas.drawText(statusText1, (width - textWidth1) / 2, frame.top
            - statusPaddingTop, paint);

    int textWidth2 = (int) paint.measureText(statusText2);
    canvas.drawText(statusText2, (width - textWidth2) / 2, frame.top
            - statusPaddingTop + 60, paint);
  }
  /**
   * 绘制移动扫描线
   *
   * @param canvas
   * @param frame
   */
  private void drawScanLight(Canvas canvas, Rect frame) {

    if (scanLineTop == 0) {
      scanLineTop = frame.top;
    }

    if (scanLineTop >= frame.bottom-30) {
      scanLineTop = frame.top;
    } else {
      scanLineTop += SCAN_VELOCITY;
    }
    Rect scanRect = new Rect(frame.left, scanLineTop, frame.right,
            scanLineTop + 30);
    canvas.drawBitmap(scanLight, null, scanRect, paint);
  }
}
