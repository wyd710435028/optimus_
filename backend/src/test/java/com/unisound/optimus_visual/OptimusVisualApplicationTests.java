package com.unisound.optimus_visual;

import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

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

}
