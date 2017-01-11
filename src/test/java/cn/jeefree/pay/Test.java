package cn.jeefree.pay;
import java.util.Map;
import cn.jeefree.pay.IPay;
import cn.jeefree.pay.util.WeChatUtil;

public class Test {

	public static void main(String[] args) throws Exception {

		// 初始化接口
		IPay ipay = IPay.getInstance();

		// 微信调用统一下单
		String requestString = ipay.createPay("wechatPay", "app", "1","201701021844", "测试支付");
		// 微信调用发送请求
		String result = ipay.httpsPayRequest("wechatPay", "app", requestString);
		System.out.println(result);

		// 微信调用统一下单
		requestString = ipay.createPay("wechatPay", "pc", "1", "201701021844","测试支付");
		// 微信调用发送请求
		result = ipay.httpsPayRequest("wechatPay", "pc", requestString);
		System.out.println(result);
		
        System.out.println(ipay.getQRCodeImge("201701021844", result,"e:/upload/qrcode/"));
		
		// zfb调用统一下单
		requestString = ipay.createPay("aliPay", "pc", "1", "2017010218894","测试支付");
		System.out.println(requestString);
		// zfb调用发送请求
		result = ipay.httpsPayRequest("aliPay", "pc", requestString);
		
		
		System.out.println(result);
		
		//new Test().execute();

	}

	public void execute() throws Exception {

		
		/**
		 * 获取用户扫描二维码后，微信返回的信息
		 */
		
		String str = "<xml><appid><![CDATA[wx2421b1c4370ec43b]]></appid><attach><![CDATA[支付测试]]></attach><bank_type><![CDATA[CFT]]></bank_type><fee_type><![CDATA[CNY]]></fee_type><is_subscribe><![CDATA[Y]]></is_subscribe><mch_id><![CDATA[10000100]]></mch_id><nonce_str><![CDATA[5d2b6c2a8db53831f7eda20af46e531c]]></nonce_str><openid><![CDATA[oUpF8uMEb4qRXf22hE3X68TekukE]]></openid><out_trade_no><![CDATA[1409811653]]></out_trade_no><result_code><![CDATA[SUCCESS]]></result_code><return_code><![CDATA[SUCCESS]]></return_code><sign><![CDATA[B552ED6B279343CB493C5DD0D78AB241]]></sign><sub_mch_id><![CDATA[10000100]]></sub_mch_id><time_end><![CDATA[20140903131540]]></time_end><total_fee>1</total_fee><trade_type><![CDATA[JSAPI]]></trade_type><transaction_id><![CDATA[1004400740201409030005092168]]></transaction_id></xml>";
		
//		InputStream inStream = request.getInputStream();
//		ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
//		byte[] buffer = new byte[1024];
//		int len = 0;
//		while ((len = inStream.read(buffer)) != -1) {
//			outSteam.write(buffer, 0, len);
//		}
//		outSteam.close();
//		inStream.close();
//		String result = new String(outSteam.toByteArray(), "utf-8");
		/**
		 * 获取返回的信息内容中各个参数的值
		 */
//		Map<String, String> map = WeChatUtil.doXMLParse(str);
//		SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
//		parameters.put("appid", map.get("appid"));
//		parameters.put("openid", map.get("openid"));
//		parameters.put("mch_id", map.get("mch_id"));
//		parameters.put("is_subscribe", map.get("is_subscribe"));
//		parameters.put("nonce_str", map.get("nonce_str"));
//		parameters.put("product_id", map.get("product_id"));
//		String sign = WeChatUtil.createSign("utf-8", parameters);

//		/**
//		 * 调用统一接口，返回预付id
//		 */
//		SortedMap<Object, Object> para = new TreeMap<Object, Object>();
//		para.put("appid", PayConfig.APPID);
//		para.put("mch_id", PayConfig.MCH_ID);
//		para.put("nonce_str", WeChatUtil.CreateNoncestr());
//		para.put("body", "测试NATIVE支付");
//		para.put("out_trade_no", "20150106003");// 商户订单号要唯一
//		para.put("total_fee", "1");
//		para.put("spbill_create_ip", "127.0.0.1");
//		para.put("notify_url", PayConfig.NOTIFY_URL);// 支付成功后回调的action，与JSAPI相同
//		para.put("trade_type", "NATIVE");
//		para.put("product_id", map.get("product_id"));
//		String nativeSign = WeChatUtil.createSign("UTF-8", para);
//		para.put("sign", nativeSign);
//		String requestXML = WeChatUtil.getRequestXml(para);
//		String nativeResult = WeChatUtil.httpsRequest(PayConfig.UNIFIED_ORDER_URL, "POST", requestXML);
//		System.out.println(nativeResult);
		Map<String, String> resultMap = WeChatUtil.doXMLParse(str);
		String returnCode = (String) resultMap.get("return_code");
		String resultCode = (String) resultMap.get("result_code");
		
		System.out.println(returnCode);
		System.out.println(resultCode);
		
		System.out.println((String) resultMap.get("out_trade_no"));
		System.out.println(resultCode);
		
		/**
		 * 发送信息给微信服务器
		 */
//		SortedMap<Object, Object> toWX = new TreeMap<Object, Object>();
//		if (returnCode.equalsIgnoreCase("SUCCESS")
//				&& resultCode.equalsIgnoreCase("SUCCESS")) {
//			String prepayId = (String) resultMap.get("prepay_id");
//			toWX.put("return_code", "SUCCESS");
//			toWX.put("return_msg", "");
//			toWX.put("appid", map.get("appid"));
//			toWX.put("nonce_str", WeChatUtil.CreateNoncestr());
//			toWX.put("prepay_id", prepayId);
//			String toWXSign = "";
//			if (map.get("sign").equals(sign)) {
//				toWX.put("result_code", "SUCCESS");
//				toWXSign = WeChatUtil.createSign("utf-8", toWX);
//
//			} else {// else的部分 暂测试未通过
//				toWX.put("result_code", "FAIL");
//				toWX.put("err_code_des", "订单失效"); // result_code为FAIL时，添加该键值对，value值是微信告诉客户的信息
//				toWXSign = WeChatUtil.createSign("utf-8", toWX);
//			}
//			toWX.put("sign", toWXSign);
//			response.getWriter().write(WeChatUtil.getRequestXml(toWX));
//			System.out.println(WeChatUtil.getRequestXml(toWX));
//		} else {
//			System.out.println("付款失败！");
//			return;
//		}
	}
}
