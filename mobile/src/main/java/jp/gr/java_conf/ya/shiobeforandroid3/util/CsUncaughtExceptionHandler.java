package jp.gr.java_conf.ya.shiobeforandroid3.util; // Copyright (c) 2013-2016 YA <ya.androidapp@gmail.com> All rights reserved.

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;

import jp.gr.java_conf.ya.shiobeforandroid3.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

// http://yan-note.blogspot.jp/2010/10/android.html
public final class CsUncaughtExceptionHandler implements java.lang.Thread.UncaughtExceptionHandler {
	private static Context context = null;
	private static final String BUG_FILE = "BugReportOfS4A";
	private static final String MAILADDRESS = "ya.androidapp@gmail.com";
	private static final UncaughtExceptionHandler sDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();

	/** 
	 * バグレポートの内容をメールで送信します。 
	 * @param activity 
	 */
	public static final void SendBugReport(final Activity activity) {
		//バグレポートがなければ以降の処理を行いません。  
		final File bugfile = activity.getFileStreamPath(BUG_FILE);
		if (!bugfile.exists()) {
			return;
		}
		//AlertDialogを表示します。  
		new AlertDialog.Builder(context).setTitle(R.string.bugreport).setMessage(R.string.bugreport_dialog_message).setCancelable(true).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			@Override
			public final void onClick(final DialogInterface dialog, final int which) {
				SendMail(activity, bugfile);
			}
		}).setNegativeButton(R.string.no, null).create().show();
	}

	/** 
	 * バグレポートの内容をメールで送信します。 
	 * @param activity 
	 * @param bugfile 
	 */
	private static final void SendMail(final Activity activity, File bugfile) {
		//バグレポートの内容を読み込みます。  
		final StringBuilder sb = new StringBuilder();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(bugfile));
			String str;
			while (( str = br.readLine() ) != null) {
				sb.append(str + "\n");
			}
		} catch (final Exception e) {
			WriteLog.write(context, e);
		}
		if (br != null) {
			try {
				br.close();
			} catch (final IOException e) {
				WriteLog.write(context, e);
			}
		}
		//メールで送信します。  
		final Intent intent = new Intent();
		intent.setAction(Intent.ACTION_SENDTO);
		intent.setData(Uri.parse("mailto:" + MAILADDRESS));
		intent.putExtra(Intent.EXTRA_SUBJECT, "[BugReport]" + R.string.app_name);
		intent.putExtra(Intent.EXTRA_TEXT, sb.toString());
		activity.startActivity(intent);
		//バグレポートを削除します。  
		bugfile.delete();
	}

	/** 
	 * コンストラクタ 
	 * @param c 
	 */
	public CsUncaughtExceptionHandler(final Context c) {
		context = c;
	}

	/** 
	 * キャッチされない例外によって指定されたスレッドが終了したときに呼び出されます 
	 * 例外スタックトレースの内容をファイルに出力します 
	 */
	@SuppressLint("WorldReadableFiles")
	public final void uncaughtException(final Thread thread, final Throwable ex) {
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(context.openFileOutput(BUG_FILE, Context.MODE_WORLD_READABLE));
			ex.printStackTrace(pw);
		} catch (final FileNotFoundException e) {
			WriteLog.write(context, e);
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
		sDefaultHandler.uncaughtException(thread, ex);
	}

}
