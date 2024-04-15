package com.unisound.optimus_visual;

import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootTest
class OptimusVisualApplicationTests {

	@Test
	void contextLoads() throws IOException {
		//浏览器设置
		WebClient webClient = new WebClient();
		webClient.setAjaxController(new NicelyResynchronizingAjaxController());
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.getOptions().setCssEnabled(true);
		webClient.getOptions().setJavaScriptEnabled(true);
		webClient.getOptions().setActiveXNative(false);

		//打开页面
//		HtmlPage page = webClient.getPage("http://10.128.1.57:8033/login");


//		//鼠标悬浮到周榜上
//		DomElement inputEle = page.getFirstByXPath("//div[@id='rank']//li[@data-id='2']");
//		page = (HtmlPage) inputEle.mouseOver();
//		DomElement ulElement = page.getFirstByXPath("//div[@id='rank']//ul[@id='d-2']");
//
//		//周榜信息
//		System.out.println(ulElement.asNormalizedText());
	}

	@Test
	public void testRegex(){
		String regex = "[\\u4e00-\\u9fff&&[^一]&&[^无]]";
		String text = "我是一，一二(一)三无一.一、一一一一一周后随访一无一一一一一一一，你好吗？";
		Matcher matcher = Pattern.compile(regex, Pattern.UNICODE_CHARACTER_CLASS).matcher(text);
		while (matcher.find()) {
			System.out.println(matcher.group());
		}
	}

}
