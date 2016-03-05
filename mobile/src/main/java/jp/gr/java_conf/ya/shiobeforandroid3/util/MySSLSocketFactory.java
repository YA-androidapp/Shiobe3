package jp.gr.java_conf.ya.shiobeforandroid3.util; // Copyright (c) 2013-2016 YA <ya.androidapp@gmail.com> All rights reserved.

// http://www.glamenv-septzen.net/view/981 より

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ssl.SSLSocketFactory;

final class MySSLSocketFactory extends SSLSocketFactory {

	final SSLContext sslContext = SSLContext.getInstance("TLS");

	MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
		super(truststore);

		// 自己署名証明書を受け付けるカスタムSSLContextの準備
		final TrustManager tm = new X509TrustManager() {
			@Override
			public void checkClientTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
			}

			@Override
			public void checkServerTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};
		sslContext.init(null, new TrustManager[] { tm }, null);
	}

	@Override
	public Socket createSocket() throws IOException {
		// カスタムSSLContext経由で生成したSSLソケットを返す。
		return sslContext.getSocketFactory().createSocket();
	}

	@Override
	public Socket createSocket(final Socket socket, final String host, final int port, final boolean autoClose) throws IOException, UnknownHostException {
		// カスタムSSLContext経由で生成したSSLソケットを返す。
		return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
	}
}