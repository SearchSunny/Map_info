package com.mapbar.info.collection.upload;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.mapbar.info.collection.util.BitmapUtils;
import com.mapbar.info.collection.util.LogPrint;

/**
 * 上传文件到服务器
 * 
 * @author Administrator
 * 
 */
public class FileUtil {
	private Context context;
	private DefaultHttpClient httpclient;

	/**
	 * 限制上传图处的大小 K
	 */
	private final int SIZE = 400;
	
	public FileUtil(Context context) {
		this.context = context;
	}

	public void cancel() {
		if (httpclient != null) {
			httpclient.getConnectionManager().shutdown();
		}
	}

	/**
	 * 上传任务采集点
	 * @param path 上传URL
	 * @param params 采集点信息
	 * @param files 图片文件
	 * @return
	 */
	public boolean post(String path, Map<String, String> params, FormFile[] files) {
		MultipartEntity reqEntity = new MultipartEntity();
		httpclient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(path);
		//httpPost.setHeader("Cookie", StringUtils.getLoginSessionId(context) + ";" + StringUtils.getLoginToken(context));
		if (files != null && files.length > 0) {
			for (int i = 0; i < files.length; i++) {

				FileBody file = new FileBody(files[i].getFile());
				reqEntity.addPart("fileUpload",file);
			}

		}

		if (params != null && params.size() > 0) {

			for (Map.Entry<String, String> entry : params.entrySet()) {// 构造文本类型参数的实体数据

				try {
                    Log.e("fileUtil", "entry.getValue()="+entry.getValue());
					reqEntity.addPart(entry.getKey(), new StringBody(entry.getValue(), Charset.forName("UTF-8")));
					 Log.e("fileUtil", "entry.()="+entry.getValue());
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		httpPost.setEntity(reqEntity);

		HttpResponse response;
		try {
			response = httpclient.execute(httpPost);
			Log.e("upload", "Error 0000");
			if (response.getStatusLine().getStatusCode() == 200) {
				Log.e("upload", "20000");
				InputStream in = response.getEntity().getContent();
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				String tempbf;
				StringBuffer sb = new StringBuffer(100);
				while ((tempbf = br.readLine()) != null) {
					sb.append(tempbf);
				}
				try {
					
					JSONObject jsonObject = new  JSONObject(sb.toString());
					boolean result = jsonObject.getBoolean("result");
					LogPrint.Print("postmessage=="+jsonObject.getString("message"));
					if (result) {
						
						return true;
						
					}else {
						
						return false;
					}
					
				} catch (Exception e) {
					
					e.printStackTrace();
				}
			}

		} catch (ClientProtocolException e1) {
			e1.printStackTrace();

		} catch (IOException e1) {
			e1.printStackTrace();

		} finally {

			httpclient.getConnectionManager().shutdown();
		}
		return false;
	}

	
	/**
	 * 上传任务采集点
	 * @param path 上传URL
	 * @param params 采集点信息
	 * @param files 图片文件
	 * @return
	 */
	public String postFile(String path, Map<String, String> params, FormFile[] files) {
		MultipartEntity reqEntity = new MultipartEntity();
		httpclient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(path);
		//httpPost.setHeader("Cookie", StringUtils.getLoginSessionId(context) + ";" + StringUtils.getLoginToken(context));
		if (files != null && files.length > 0) {
			for (int i = 0; i < files.length; i++) {

				FileBody file = new FileBody(files[i].getFile());
				reqEntity.addPart("fileUpload",file);
			}

		}

		if (params != null && params.size() > 0) {

			for (Map.Entry<String, String> entry : params.entrySet()) {// 构造文本类型参数的实体数据

				try {
                    Log.e("fileUtil", "entry.getValue()="+entry.getValue());
					reqEntity.addPart(entry.getKey(), new StringBody(entry.getValue(), Charset.forName("UTF-8")));
					 Log.e("fileUtil", "entry.()="+entry.getValue());
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		httpPost.setEntity(reqEntity);

		HttpResponse response;
		try {
			response = httpclient.execute(httpPost);
			Log.e("upload", "Error 0000");
			InputStream in = response.getEntity().getContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String tempbf;
			StringBuffer sb = new StringBuffer(100);
			while ((tempbf = br.readLine()) != null) {
				sb.append(tempbf);
			}
			return sb.toString();

		} catch (ClientProtocolException e1) {
			e1.printStackTrace();

		} catch (IOException e1) {
			e1.printStackTrace();

		} finally {

			httpclient.getConnectionManager().shutdown();
		}
		return null;
	}
	
	/**
	 * 压缩图片
	 * 上传任务采集点
	 * @param path 上传URL
	 * @param params 采集点信息
	 * @param files 图片文件
	 * @return
	 */
	public String postFileCompress(String path, Map<String, String> params, FormFile[] files) {
		MultipartEntity reqEntity = new MultipartEntity();
		httpclient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(path);
		//httpPost.setHeader("Cookie", StringUtils.getLoginSessionId(context) + ";" + StringUtils.getLoginToken(context));
		if (files != null && files.length > 0) {
			for (int i = 0; i < files.length; i++) {
				LogPrint.Print("fileupload","------当前文件大小--------" + (files[i].getFile().length() / 1024));
				//判断文件大小是否超过120K
				if ((files[i].getFile().length() / 1024) > SIZE) {
					
					//将文件转换为byte数组
					/*byte[] data = getBytesFromFile(files[i].getFile());
					//压缩图片(对于其它手机有问题，暂时放弃)
					byte[] fileData = BitmapUtils.compressBitmap(data,120);
					if (fileData != null) {
						
						File compressFile = getFileFromBytes(fileData, files[i].getFile().getAbsolutePath());
						FileBody file = new FileBody(compressFile);
						reqEntity.addPart("fileUpload",file);
						
					}*/
					//-------------------------
					Bitmap tempBitmap = BitmapUtils.getimage(files[i].getFile().getAbsolutePath(), SIZE);
					byte[] fileData = BitmapUtils.compressBitmap(tempBitmap);
					if (fileData != null) {
						
						File compressFile = getFileFromBytes(fileData, files[i].getFile().getAbsolutePath());
						long fileSize = (compressFile.length() / 1024);
						LogPrint.Print("fileupload","------压缩之后的图片大小--------" + fileSize);
						FileBody file = new FileBody(compressFile);
						reqEntity.addPart("fileUpload",file);
						
					}
				}
				else {
					
					FileBody file = new FileBody(files[i].getFile());
					reqEntity.addPart("fileUpload",file);
				}
				
				
			}

		}

		if (params != null && params.size() > 0) {

			for (Map.Entry<String, String> entry : params.entrySet()) {// 构造文本类型参数的实体数据

				try {
                    Log.e("fileUtil", "entry.getValue()="+entry.getValue());
					reqEntity.addPart(entry.getKey(), new StringBody(entry.getValue(), Charset.forName("UTF-8")));
					 Log.e("fileUtil", "entry.()="+entry.getValue());
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		httpPost.setEntity(reqEntity);

		HttpResponse response;
		try {
			response = httpclient.execute(httpPost);
			Log.e("upload", "Error 0000");
			InputStream in = response.getEntity().getContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String tempbf;
			StringBuffer sb = new StringBuffer(100);
			while ((tempbf = br.readLine()) != null) {
				sb.append(tempbf);
			}
			return sb.toString();

		} catch (ClientProtocolException e1) {
			e1.printStackTrace();

		} catch (IOException e1) {
			e1.printStackTrace();

		} finally {

			httpclient.getConnectionManager().shutdown();
		}
		return null;
	}
	/**
	 * 校验图片是否上传成功
	 * @param path 图片路径
	 * @return
	 */
	public long postCheckImage(String path) {
		MultipartEntity reqEntity = new MultipartEntity();
		httpclient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(path);

		httpPost.setEntity(reqEntity);

		HttpResponse response;
		long imageLength = -1;
		try {
			response = httpclient.execute(httpPost);
			LogPrint.Print("fileupload","getStatusCode()=="+response.getStatusLine().getStatusCode());
			
			if(response.getStatusLine().getStatusCode() == 200){
				InputStream in = response.getEntity().getContent();
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				String tempbf;
				StringBuffer sb = new StringBuffer(100);
				while ((tempbf = br.readLine()) != null) {
					
					sb.append(tempbf);
				}
				LogPrint.Print("fileupload","postCheckImage--校验图片是否上传成功=="+sb.toString());
				//按照后台需求返回内容为200和400，200有图片，400没有图片
				if (sb.toString().equals("200")) {
					
					imageLength = 1;
				}
				else if (sb.toString().equals("400")) {
					
					imageLength = -1;
				}
				/*long imageLength = response.getEntity().getContentLength();
				LogPrint.Print("fileupload","postCheckImage--校验图片是否上传成功=="+imageLength);*/
				return imageLength;
				
			}

		} catch (ClientProtocolException e1) {
			e1.printStackTrace();

		} catch (IOException e1) {
			e1.printStackTrace();

		} finally {

			httpclient.getConnectionManager().shutdown();
		}
		return imageLength;
	}
	/**
	 * 直接通过HTTP协议提交数据到服务器,实现如下面表单提交功能: <FORM METHOD=POST
	 * ACTION="http://192.168.1.101:8083/upload/servlet/UploadServlet"
	 * enctype="multipart/form-data"> <INPUT TYPE="text" NAME="name"> <INPUT
	 * TYPE="text" NAME="id"> <input type="file" name="imagefile"/> <input
	 * type="file" name="zip"/> </FORM>
	 * 
	 * @param path
	 *            上传路径(注：避免使用localhost或127.0.0.1这样的路径测试，因为它会指向手机模拟器，你可以使用http://
	 *            www.iteye.cn或http://192.168.1.101:8083这样的路径测试)
	 * @param params
	 *            请求参数 key为参数名,value为参数值
	 * @param file
	 *            上传文件
	 */

	public String postPic(String path, FormFile files) throws Exception {
		final String BOUNDARY = "---------------------------7da2137580612"; // 数据分隔线
		final String endline = "--" + BOUNDARY + "--\r\n";// 数据结束标志

		int fileDataLength = 0;
		if (files != null) {// 得到文件类型数据的总长度
			StringBuilder fileExplain = new StringBuilder();
			fileExplain.append("--");
			fileExplain.append(BOUNDARY);
			fileExplain.append("\r\n");
			fileExplain.append("Content-Disposition: form-data;name=\"" + files.getParameterName() + "\";filename=\""
					+ files.getFilname() + "\"\r\n");
			fileExplain.append("Content-Type: " + files.getContentType() + "\r\n\r\n");
			fileExplain.append("\r\n");
			fileDataLength += fileExplain.length();
			if (files.getInStream() != null) {
				fileDataLength += files.getFile().length();
			} else {
				fileDataLength += files.getData().length;
			}
		}
		StringBuilder textEntity = new StringBuilder();

		// 计算传输给服务器的实体数据总长度
		int dataLength = textEntity.toString().getBytes().length + fileDataLength + endline.getBytes().length;

		URL url = new URL(path);
		int port = url.getPort() == -1 ? 80 : url.getPort();
		Socket socket = new Socket(InetAddress.getByName(url.getHost()), port);
		OutputStream outStream = socket.getOutputStream();
		// 下面完成HTTP请求头的发送
		// String requestmethod = "POST " + url.getPath() + "?token="
		// + share.getString(MGisSharedPreferenceConstant.TOKEN)
		// + " HTTP/1.1\r\n";
		// outStream.write(requestmethod.getBytes());
		String accept = "Accept: image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*\r\n";
		outStream.write(accept.getBytes());
		String language = "Accept-Language: zh-CN\r\n";
		outStream.write(language.getBytes());
		String contenttype = "Content-Type: multipart/form-data; boundary=" + BOUNDARY + "\r\n";
		outStream.write(contenttype.getBytes());
		String contentlength = "Content-Length: " + dataLength + "\r\n";

		outStream.write(contentlength.getBytes());
		// String session = share
		// .getString(MGisSharedPreferenceConstant.SESSION_ID);
		// if (session != null) {
		//
		// String cookie = "Cookie: JSESSIONID=" + session + "\r\n";
		// outStream.write(cookie.getBytes());
		// }

		String alive = "Connection: Keep-Alive\r\n";
		outStream.write(alive.getBytes());
		String host = "Host: " + url.getHost() + ":" + port + "\r\n";
		outStream.write(host.getBytes());
		// 写完HTTP请求头后根据HTTP协议再写一个回车换行
		outStream.write("\r\n".getBytes());
		// 把所有文本类型的实体数据发送出来
		outStream.write(textEntity.toString().getBytes());
		// 把所有文件类型的实体数据发送出来
		if (files != null) {
			StringBuilder fileEntity = new StringBuilder();
			fileEntity.append("--");
			fileEntity.append(BOUNDARY);
			fileEntity.append("\r\n");
			fileEntity.append("Content-Disposition: form-data;name=\"" + files.getParameterName() + "\";filename=\""
					+ files.getFilname() + "\"\r\n");
			fileEntity.append("Content-Type: " + files.getContentType() + "\r\n\r\n");
			outStream.write(fileEntity.toString().getBytes());
			if (files.getInStream() != null) {
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = files.getInStream().read(buffer, 0, 1024)) != -1) {
					outStream.write(buffer, 0, len);
				}
				files.getInStream().close();
			} else {
				outStream.write(files.getData(), 0, files.getData().length);
			}
			outStream.write("\r\n".getBytes());
		}
		// 下面发送数据结束标志，表示数据已经结束
		outStream.write(endline.getBytes());
		InputStream is = socket.getInputStream();

		BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		StringBuffer sb = new StringBuffer();
		String line;
		boolean isData = false;
		while ((line = reader.readLine()) != null) {

			if (isData) {
				return line;
			}
			if (line.trim().length() == 0) {
				isData = true;
			}

		}

		// if (reader.readLine().indexOf("200") == -1)
		// {// 读取web服务器返回的数据，判断请求码是否为200，如果不是200，代表请求失败
		// return false;
		// }
		outStream.flush();
		outStream.close();
		reader.close();
		socket.close();

		return null;
	}
	
	/**
	 * 返回一个byte数组
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private  byte[] getBytesFromFile(File file){

		byte[] bytes = null;
		try {
			InputStream is = new FileInputStream(file);

			// 获取文件大小
			long length = file.length();

			if (length > Integer.MAX_VALUE) {

				// 文件太大，无法读取
				throw new IOException("File is to large " + file.getName());

			}

			// 创建一个数据来保存文件数据

			bytes = new byte[(int) length];

			// 读取数据到byte数组中

			int offset = 0;

			int numRead = 0;

			while (offset < bytes.length

			&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {

				offset += numRead;

			}

			// 确保所有数据均被读取

			if (offset < bytes.length) {

				throw new IOException("Could not completely read file "
						+ file.getName());

			}

			// Close the input stream and return bytes

			is.close();
		} catch (Exception e) {

           e.printStackTrace();
		}

		return bytes;

	}
	
	/** 
     * 把字节数组保存为一个文件 
     *  
     * @param b 
     * @param outputFile 
     * @return 
     */  
    public static File getFileFromBytes(byte[] b, String outputFile) {  
        File ret = null;  
        BufferedOutputStream stream = null;  
        try {  
            ret = new File(outputFile);  
            FileOutputStream fstream = new FileOutputStream(ret);  
            stream = new BufferedOutputStream(fstream);  
            stream.write(b);  
        } catch (Exception e) {  
            // log.error("helper:get file from byte process error!");  
            e.printStackTrace();  
        } finally {  
            if (stream != null) {  
                try {  
                    stream.close();  
                } catch (IOException e) {  
                    // log.error("helper:get file from byte process error!");  
                    e.printStackTrace();  
                }  
            }  
        }  
        return ret;  
    }
}