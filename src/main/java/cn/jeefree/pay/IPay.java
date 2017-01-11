package cn.jeefree.pay;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.jeefree.pay.config.PayConfig;
import cn.jeefree.pay.core.AlipayNotify;
import cn.jeefree.pay.core.AlipaySubmit;
import cn.jeefree.pay.util.PropKit;
import cn.jeefree.pay.util.UtilOther;
import cn.jeefree.pay.util.WeChatUtil;
import cn.jeefree.pay.util.ZxingUtils;

public class IPay {
	
	
	
	
	private static IPay ipay = new IPay();
	
    private IPay (){
    	PropKit.use("config.properties");
    }
    
    public static IPay getInstance() {
    	return ipay;
    }
	

	/**
	 * 创建预付费订单
	 * 
	 * @param payType
	 *            支付类型 aliPay wechatPay unionPay
	 * @param clientType
	 *            客户端类型 pc app
	 * @param payBalance
	 *            支付金额 精确到分
	 * @param tradeNo
	 *            唯一订单编号
	 * @param tradeName
	 *            商品名称
	 * @return
	 */
	public String createPay(String payType, String clientType,
			String payBalance, String tradeNo, String tradeName) {

		if (UtilOther.notBlank(payType, clientType, payBalance, tradeNo,
				tradeName)) {

			if (payType.equals("aliPay")) {

				// 把请求参数打包成数组
				Map<String, String> sParaTemp = new HashMap<String, String>();
				sParaTemp.put("service", PayConfig.service);
				sParaTemp.put("partner", PayConfig.partner);
				sParaTemp.put("seller_id", PayConfig.seller_id);
				sParaTemp.put("_input_charset", PayConfig.input_charset);
				sParaTemp.put("payment_type", PayConfig.payment_type);
				sParaTemp.put("notify_url", PayConfig.notify_url);
				sParaTemp.put("return_url", PayConfig.return_url);
				sParaTemp.put("out_trade_no", tradeNo);
				sParaTemp.put("subject", tradeName);
				sParaTemp.put("total_fee", UtilOther.fenToYuan(payBalance));
				sParaTemp.put("body", tradeName);

				// 建立请求
				return AlipaySubmit.buildRequest(sParaTemp, "post", "确认");

			} else if (payType.equals("wechatPay")) {

				SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
				parameters.put("appid", PayConfig.APPID);
				parameters.put("mch_id", PayConfig.MCH_ID);
				if (clientType.equals("app")) {
					parameters.put("trade_type", "APP");
				} else {
					parameters.put("trade_type", "NATIVE");
				}
				parameters.put("spbill_create_ip", UtilOther.getLocalIP());
				parameters.put("product_id", tradeNo);
				parameters.put("out_trade_no", tradeNo);
				parameters.put("body", tradeName);
				parameters.put("total_fee", payBalance);
				parameters.put("notify_url", PayConfig.NOTIFY_URL);
				parameters.put("nonce_str", WeChatUtil.CreateNoncestr());
				parameters.put("sign",
						WeChatUtil.createSign("UTF-8", parameters));
				return WeChatUtil.getRequestXml(parameters);

			} else if (payType.equals("unionPay")) {

				return null;
			} else {
				return "支付类型 aliPay wechatPay unionPay";
			}

		} else {

			return "参数不能为空";
		}

	}

	/**
	 * 预付费订单支付请求
	 * 
	 * @param payType
	 *            支付类型 aliPay wechatPay unionPay
	 * @param clientType
	 *            客户端类型 pc app
	 * @param requestString
	 *            统一下单返回的数据报
	 * @return
	 */
	public String httpsPayRequest(String payType, String clientType,
			String requestString) {

		if (UtilOther.notBlank(payType, clientType, requestString)) {
			if (payType.equals("aliPay")) {

				return "";

			} else if (payType.equals("wechatPay")) {

				String resultString = WeChatUtil.httpsRequest(
						PayConfig.UNIFIED_ORDER_URL, "POST", requestString);

				Map<String, String> map = WeChatUtil.doXMLParse(resultString);

				if (clientType.equals("app")) {// app微信返回的是prepay_id

					if (map.get("return_code").equalsIgnoreCase("FAIL")) {
						return map.get("return_msg");
					} else if (map.get("return_code").equalsIgnoreCase(
							"SUCCESS")
							&& map.get("result_code").equalsIgnoreCase(
									"SUCCESS")) {
						return map.get("prepay_id");

					} else {
						return map.get("err_code_des");

					}

				} else {// PC微信返回的是二维码

					if (map.get("return_code").equalsIgnoreCase("FAIL")) {
						return map.get("return_msg");
					} else if (map.get("return_code").equalsIgnoreCase(
							"SUCCESS")
							&& map.get("result_code").equalsIgnoreCase(
									"SUCCESS")) {
						return map.get("code_url");

					} else {
						return map.get("err_code_des");

					}
				}

			} else if (payType.equals("unionPay")) {

				return null;
			} else {
				return "支付类型 aliPay wechatPay unionPay";
			}

		} else {
			return "参数不能为空";
		}
	}

	/**
	 * 处理异步返回支付结果 接收返回封装返回结果Map
	 * 
	 * @param payType
	 *            支付类型
	 * @param request
	 *            Request对象
	 * @param response
	 *            Response对象
	 * @return
	 */
	public Map<String, String> httpsPayResponse(String payType,
			HttpServletRequest request, HttpServletResponse response) {

		Map<String, String> returnMap = new HashMap<String, String>();

		if (payType.equals("aliPay")) {

			// 获取支付宝POST过来反馈信息
			Map<String, String> params = new HashMap<String, String>();
			Map<String, String[]> requestParams = request.getParameterMap();
			for (Iterator<String> iter = requestParams.keySet().iterator(); iter
					.hasNext();) {
				String name = (String) iter.next();
				String[] values = (String[]) requestParams.get(name);
				String valueStr = "";
				for (int i = 0; i < values.length; i++) {
					valueStr = (i == values.length - 1) ? valueStr + values[i]
							: valueStr + values[i] + ",";
				}
				// 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
				// valueStr = new String(valueStr.getBytes("ISO-8859-1"),
				// "gbk");
				params.put(name, valueStr);
			}

			if (AlipayNotify.verify(params)) {// 验证成功

				try {
					// 商户订单号
					String out_trade_no = new String(request.getParameter(
							"out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
					// 交易状态
					String trade_status = new String(request.getParameter(
							"trade_status").getBytes("ISO-8859-1"), "UTF-8");

					returnMap.put("out_trade_no", out_trade_no);
					returnMap.put("return_url", PayConfig.return_url);
					returnMap.put("trade_status", trade_status);
					return returnMap;

				} catch (UnsupportedEncodingException e) {

					return null;

				}

			} else {// 验证失败
				return null;
			}

		} else if (payType.equals("weichatPay")) {

			// 获取微信POST过来反馈信息
			try {
				Map<String, String> params = new HashMap<String, String>();
				InputStream inStream = request.getInputStream();
				ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = inStream.read(buffer)) != -1) {
					outSteam.write(buffer, 0, len);
				}
				outSteam.close();
				inStream.close();

				params = WeChatUtil.doXMLParse(new String(outSteam
						.toByteArray(), "utf-8"));

				String returnCode = (String) params.get("return_code");
				String resultCode = (String) params.get("result_code");

				if (returnCode.equalsIgnoreCase("FAIL")) {
					return null;
				} else if (returnCode.equalsIgnoreCase("SUCCESS")
						&& resultCode.equalsIgnoreCase("SUCCESS")) {

					// 商户订单号
					String out_trade_no = (String) params.get("out_trade_no");
					returnMap.put("out_trade_no", out_trade_no);
					returnMap.put("return_url", PayConfig.SUCCESS_URL);
					returnMap.put("trade_status", resultCode);
					return returnMap;
				} else {

					return null;
				}

			} catch (IOException e) {

				return null;

			}

		} else if (payType.equals("unionPay")) {

			return null;
		} else {

			return null;
		}
	}

	/**
	 * 使用订单号生成二维码
	 * 
	 * @param tradeNo
	 *            订单编号
	 * @param codeUrl
	 *            扫二维码后跳转url
	 * @param qrABSPath
	 *            保存绝对路径 例如"E:/TOMCAT/WEBAPP/IMGAES"
	 * @return
	 */
	public String getQRCodeImge(String tradeNo, String codeUrl, String qrABSPath) {

		String imagePath = String.format(qrABSPath + "%s.png", tradeNo);

		ZxingUtils.getQRCodeImge(codeUrl, 256, imagePath);

		return imagePath;
	}

}
