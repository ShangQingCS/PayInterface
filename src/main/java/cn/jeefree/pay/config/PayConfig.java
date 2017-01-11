package cn.jeefree.pay.config;


import cn.jeefree.pay.util.PropKit;

public class PayConfig {
	   
	    //支付宝支付参数
	   
		// 合作身份者ID，签约账号
		public  static String partner = PropKit.get("ZFB_APPID");
		// 收款支付宝账号
		public  static String seller_id = PropKit.get("ZFB_MACHINEID");
		// MD5密钥，安全检验码，由数字和字母组成的32位字符串，查看地址：https://b.alipay.com/order/pidAndKey.htm
	    public  static String key = PropKit.get("ZFB_API_KEY");
	    
	    //商户的私钥,需要PKCS8格式，RSA公私钥生成：https://doc.open.alipay.com/doc2/detail.htm?spm=a219a.7629140.0.0.nBDxfy&treeId=58&articleId=103242&docType=1
		public static String private_key = PropKit.get("ZFB_PRIVATE_KEY");
		
		// 支付宝的公钥,查看地址：https://b.alipay.com/order/pidAndKey.htm
		public static String alipay_public_key  = PropKit.get("ZFB_PUBLIC_KEY");

	    
		// 服务器异步通知页面
		public  static String notify_url = PropKit.get("ZFB_NOTIFY_URL");
		// 页面跳转同步通知页面
		public  static String return_url = PropKit.get("ZFB_RETURN_URL");
		// 签名方式
		public  static String sign_type = PropKit.get("ZFB_SIGIN_TYPE");
		// 字符编码格式  utf-8
		public  static String input_charset = "utf-8";
		// 支付类型 
		public  static String payment_type = "1";
		// 调用的接口名
		public  static String service = "create_direct_pay_by_user";
		
		//微信支付参数

		 //服务号的应用号
		 public  static String APPID = PropKit.get("WX_APPID");
		 //商户号
		 public  static String MCH_ID = PropKit.get("WX_MACHINEID");
		 //API密钥
		 public  static String API_KEY = PropKit.get("WX_API_KEY");
		 //签名加密方式
		 public  static String SIGN_TYPE = PropKit.get("WX_SIGIN_TYPE");
		 //微信支付统一接口的回调action
		 public  static String NOTIFY_URL = PropKit.get("WX_NOTIFY_URL");
		 //微信支付成功支付后跳转的地址
		 public  static String SUCCESS_URL = PropKit.get("WX_RETURN_URL");
		 //微信支付统一接口
		 public  static String UNIFIED_ORDER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
		 
		
	   
}
