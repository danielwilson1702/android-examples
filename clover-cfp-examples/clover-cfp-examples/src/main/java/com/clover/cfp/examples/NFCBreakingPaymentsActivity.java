/*
 * Copyright (C) 2018 Clover Network, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.clover.cfp.examples;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import com.clover.cfp.activity.CloverCFPActivity;

/**
 * How to use:
 * 1. Click button to break the payments app. This click rapidly toggles the device NFC adapter.
 * 2. Open a Sale, enter an amount -> click "Charge"
 * 3. Payments app is broken. It is locked and only a re-enabling NFC in the app, or rebooting fixes the issue
 * 4. Clicking the button to "fix", simply toggles NFC over a safe delay, unlocking the device for the Payments app.
 *
 * n.b., only tested on a Mini 2, other devices may differ.
 * Playing with the timings, 50ms breaks it most of the time, 20ms seems to break it all the time.
 */
public class NFCBreakingPaymentsActivity extends CloverCFPActivity {

  private static final long BREAKING_DELAY_MS = 20;
  private static final long NON_BREAKING_MINIMUM_DELAY_MS = 100;
  private NfcAdapter mAdapter;
  private PendingIntent mPendingIntent;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_nfc);

    mAdapter = NfcAdapter.getDefaultAdapter(this);
    if (mAdapter == null) {
      setResultAndFinish(Activity.RESULT_CANCELED, getText(R.string.no_nfc).toString());
      return;
    }
    setContentView(R.layout.activity_nfc_breaking_payments);

    mPendingIntent = PendingIntent.getActivity(this, 0,
            new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
  }

  @Override
  protected void onMessage(String payload) {

  }

  public void breakPayments(final View view) {
    if (mAdapter != null) {
      mAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);

      final Handler handler = new Handler(Looper.getMainLooper());
      handler.postDelayed(new Runnable() {
        @Override
        public void run() {
          mAdapter.disableForegroundDispatch(NFCBreakingPaymentsActivity.this);
          Toast.makeText(NFCBreakingPaymentsActivity.this, "Payments are now broken", Toast.LENGTH_SHORT).show();
        }
      }, BREAKING_DELAY_MS);
    }
  }

  public void fixPayments(final View view) {
    if (mAdapter != null) {
      mAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);

      final Handler handler = new Handler(Looper.getMainLooper());
      handler.postDelayed(new Runnable() {
        @Override
        public void run() {
          mAdapter.disableForegroundDispatch(NFCBreakingPaymentsActivity.this);
          Toast.makeText(NFCBreakingPaymentsActivity.this, "Payments are now fixed", Toast.LENGTH_SHORT).show();
        }
      }, NON_BREAKING_MINIMUM_DELAY_MS);
    }
  }


}
