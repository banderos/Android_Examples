package org.test.okHttpGet;

import android.app.*;
import android.os.*;
import android.widget.*;
import android.view.View.*;
import android.view.*;
import okhttp3.*;
import java.io.*;
import android.content.*;
import android.webkit.*;

public class MainActivity extends Activity 
{
	protected final static String TAG = "MainActivity";
	private Context ctx;
	private Button bGo;
	private EditText txtHtmlCodes;
	private EditText edUrl;
	private LinearLayout ltResult;
	private WebView wv;
	private HorizontalScrollView scrlContainer;

	private View.OnClickListener onClickListener = new View.OnClickListener(){

		@Override
		public void onClick(View p1)
		{
			final Handler hnd = new Handler(){
				public void handleMessage(Message msg) {
					
					int progress = msg.getData().getInt("progress");

                    switch(progress){
                        case Downloader.ProgressStatus.CONNECTING:

                            break;
                        case Downloader.ProgressStatus.RESOLVING:

                            break;
                        case Downloader.ProgressStatus.FETCHING:

                            break;
                        case Downloader.ProgressStatus.DONE:
							String page = msg.getData().getString("page");
                            txtHtmlCodes.setText(page);
						//	wv.loadData(page,"txt/html","utf8");
							wv.getSettings().setJavaScriptEnabled(true);

						//	final Activity activity = this;
							wv.setWebChromeClient(new WebChromeClient() {
									public void onProgressChanged(WebView view, int progress) {
										// Activities and WebViews measure progress with different scales.
										// The progress meter will automatically disappear when we reach 100%
								//		activity.setProgress(progress * 1000);
									}
								});
							wv.setWebViewClient(new WebViewClient() {
									public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
								//		Toast.makeText(activity, "Oh no! " + description, Toast.LENGTH_SHORT).show();
									}
								});

							wv.loadUrl(edUrl.getText().toString());
							
                            break;
                    }
				}
			};
			Bundle params = new Bundle();
			params.putString("url", edUrl.getText().toString());
			Downloader dl = new Downloader(params,hnd);
			dl.start();
			try
			{
				while (!dl.isAlive())
					Thread.sleep(1000);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}

		
	};
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		ctx = getApplicationContext();
		
		bGo = (Button) findViewById(R.id.bGo);
		bGo.setOnClickListener(onClickListener);
		txtHtmlCodes = (EditText) findViewById(R.id.txtHtmlCodes);
		edUrl = (EditText) findViewById(R.id.edUrl);
		scrlContainer = (HorizontalScrollView) findViewById(R.id.scrlContainer);
		ltResult = (LinearLayout) findViewById(R.id.ltResult);
		wv = new WebView(ctx);
	//	wv.setVisibility(View.GONE);
		ltResult.addView(wv);
    }
	/**<h2>Описание</h2>
	 * <p></p>
	 */
	public class Downloader extends Thread{
		
		protected final static String TAG = "Downloader";
		private String url;
		private Handler HNDL;
		
		public class ProgressStatus
		{

			public static final int CONNECTING = 0;

			public static final int RESOLVING = 10;

			public static final int FETCHING = 30;

			public static final int DONE = 100;
			
		}
		Downloader(Bundle params, Handler hndl){
			HNDL = hndl;
			url = params.getString("url");
		}

		public void run(){
			Message msg = HNDL.obtainMessage();
			Bundle bnd = new Bundle();
			OkHttpClient client = new OkHttpClient();
			Request request = new Request.Builder()
				.url(url)
				.build();
			try
			{
				Response response = client.newCall(request).execute();
				bnd.putInt("progress", ProgressStatus.DONE);
				bnd.putString("page", response.body().string());
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			msg.setData(bnd);
			HNDL.sendMessage(msg);
		}
	}
}
