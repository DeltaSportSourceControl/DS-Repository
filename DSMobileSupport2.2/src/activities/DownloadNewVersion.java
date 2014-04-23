package activities;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import util.Enums;
import util.MessageManager;
import webservice.WSOGetLatestVersion;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.example.dsmobilesupport.R;

public class DownloadNewVersion extends Activity implements OnClickListener {

	private String webResponse;
	private Handler handler = new Handler();
	private String downloadUrl;
	private File downloadFile = new File(Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/download/");
	private String application;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_download_new_version);

		View btnDownload22 = findViewById(R.id.btnDownload2_2);
		View btnDownload43 = findViewById(R.id.btnDownload4_3);

		btnDownload22.setOnClickListener(this);
		btnDownload43.setOnClickListener(this);
		System.out.print("testiranje");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_search:
			startActivity(new Intent(this, GetArticleState.class));
			return true;

		case R.id.menu_preferences:
			startActivity(new Intent(this, Settings.class));
			return true;

		case R.id.menu_new_version:
			startActivity(new Intent(this, DownloadNewVersion.class));
			return true;

		case R.id.menu_exit:
			this.finish();
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnDownload2_2:
			downloadUrl = "http://mobileappsupport.deltasport.com/Download/Android2.2/DSMobileSupport2.2.apk";
			// downloadUrl = "http://mobileappsupport.deltasport.com/Download/Proba/DSMobileSupport2.2.apk";
			application = "DSMobileSupport2.2.apk";
			download(Enums.AndroidVersion.android22.ordinal());
			break;
		case R.id.btnDownload4_3:
			downloadUrl = "http://mobileappsupport.deltasport.com/Download/Android4.3/DSMobileSupport4.3.apk";
			application = "DSMobileSupport4.3.apk";
			download(Enums.AndroidVersion.android43.ordinal());
			break;
		}
	}

	private void download(int androidVersionId) {
		webResponse = "";
		if (androidVersionId == Enums.AndroidVersion.android22.ordinal()) {
			Object[] parameters = new Object[1];
			parameters[0] = androidVersionId;
			WSOGetLatestVersion wsoGetLatestVersion = new WSOGetLatestVersion(
					getApplicationContext());
			webResponse = wsoGetLatestVersion.callWebService(parameters);
		}
		handler.post(webServiceResult);
	}

	final Runnable webServiceResult = new Runnable() {
		public void run() {
			try {
				PackageInfo pInfo = getPackageManager().getPackageInfo(
						getPackageName(), 0);
				String version = pInfo.versionName;
				if (webResponse.equals(version))
					MessageManager.showShortMessage(getApplicationContext(),
							"Nema novih verzija");
				else {
					deleteOldAppFiles();

					Intent intent22 = new Intent(Intent.ACTION_VIEW,
							Uri.parse(downloadUrl));
					startActivity(intent22);

					new CountDownTimer(60000, 3000) {
						boolean finished;
						public void onTick(long millisUntilFinished) {
							if (downloadFinished() && !finished){
								finished = true;
								onFinish();
							}															
						}						

						public void onFinish() {
							Intent intent = new Intent(Intent.ACTION_VIEW);
							intent.setDataAndType(Uri.fromFile(new File(
									Environment.getExternalStorageDirectory()
											+ "/download/" + application)),
									"application/vnd.android.package-archive");
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(intent);
							cancel();
						}
					}.start();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				webResponse = "";
			}
		}
	};

	private void deleteOldAppFiles() {
		for (File file : downloadFile.listFiles()) {
			if (file.getName().startsWith("DSMobileSupport"))
				file.delete();
		}
	}
	
	private boolean downloadFinished() {
		for (File file : downloadFile.listFiles()) {
			if (file.getName().startsWith(application))
				return true;
		}
		return false;
	}
}
