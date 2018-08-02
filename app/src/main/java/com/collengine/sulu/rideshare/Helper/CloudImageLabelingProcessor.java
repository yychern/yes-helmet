// Copyright 2018 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.collengine.sulu.rideshare.Helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.collengine.sulu.rideshare.FinalActivity;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.cloud.FirebaseVisionCloudDetectorOptions;
import com.google.firebase.ml.vision.cloud.label.FirebaseVisionCloudLabel;
import com.google.firebase.ml.vision.cloud.label.FirebaseVisionCloudLabelDetector;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
//import com.collengine.sulu.rideshare.Helper.FinalActivity;
import com.collengine.sulu.rideshare.Helper.FrameMetadata;
import com.collengine.sulu.rideshare.Helper.GraphicOverlay;
import com.collengine.sulu.rideshare.MainActivity;
import com.collengine.sulu.rideshare.Helper.VisionProcessorBase;

import java.util.ArrayList;
import java.util.List;

/** Cloud Label Detector Demo. */
public class CloudImageLabelingProcessor
    extends VisionProcessorBase<List<FirebaseVisionCloudLabel>> {
  private static final String TAG = "CloudImgLabelProc";
  private Context context;

  private final FirebaseVisionCloudLabelDetector detector;

  public CloudImageLabelingProcessor(Context context) {
      this.context = context;
    FirebaseVisionCloudDetectorOptions options =
        new FirebaseVisionCloudDetectorOptions.Builder()
            .setMaxResults(10)
            .setModelType(FirebaseVisionCloudDetectorOptions.STABLE_MODEL)
            .build();

    detector = FirebaseVision.getInstance().getVisionCloudLabelDetector(options);
  }

  @Override
  protected Task<List<FirebaseVisionCloudLabel>> detectInImage(FirebaseVisionImage image) {
    return detector.detectInImage(image);
  }

  @Override
  protected void onSuccess(
      @NonNull List<FirebaseVisionCloudLabel> labels,
      @NonNull FrameMetadata frameMetadata,
      @NonNull GraphicOverlay graphicOverlay) {
    graphicOverlay.clear();
  //  Log.e(TAG, "cloud label size: " + labels.size());
    List<String> labelsStr = new ArrayList<>();
      List<String> consStr = new ArrayList<>();
    for (int i = 0; i < labels.size(); ++i) {
      FirebaseVisionCloudLabel label = labels.get(i);
      String status = label.getLabel();
      Log.e(TAG, "cloud label: " + "count " + i +"   " +  label.getLabel() +"     " + status.equalsIgnoreCase("glass") + "     " +   label.getConfidence());
      if (label.getLabel() != null) {
          labelsStr.add((label.getLabel()));
          consStr.add( String.valueOf(label.getConfidence()));

      }

    }

      if(labelsStr.contains("helmet")){

          Intent mIntent = new Intent(context, FinalActivity.class);

          int index = labelsStr.indexOf("helmet");
          String confidence = consStr.get(index);
          mIntent.putExtra("CONFIDENCE_HELMET" ,confidence);
          mIntent.putExtra("VERIFY" , "verified");
      //    Log.e(TAG, "cloud label inside if: " +  "   " + confidence);
          context.startActivity(mIntent);
          ((Activity)context).finish();
      }
//      else if(labelsStr.contains("headgear")){
//
//          Intent mIntent = new Intent(context, FinalActivity.class);
//
//          int index = labelsStr.indexOf("headgear");
//          String confidence = consStr.get(index);
//          mIntent.putExtra("CONFIDENCE_HELMET" ,confidence);
//          mIntent.putExtra("VERIFY" , "verified");
//      //    Log.e(TAG, "cloud label inside if: " +  "   " + confidence);
//          context.startActivity(mIntent);
//      }
      else{
          Intent mIntent = new Intent(context, FinalActivity.class);
//          mIntent.putExtra("CONFIDENCE_HELMET" , String.valueOf(label.getConfidence()));
          mIntent.putExtra("VERIFY" , "not-verified");
          context.startActivity(mIntent);
      }
//    CloudLabelGraphic cloudLabelGraphic = new CloudLabelGraphic(graphicOverlay);
//    graphicOverlay.add(cloudLabelGraphic);
//    cloudLabelGraphic.updateLabel(labelsStr);
  }

  @Override
  protected void onFailure(@NonNull Exception e) {
    Log.e(TAG, "Cloud Label detection failed " + e);
  }
}
