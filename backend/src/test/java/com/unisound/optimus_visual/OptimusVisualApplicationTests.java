package com.unisound.optimus_visual;

import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

	@Test
	public void testPermutations(){
		List<String> chars = new ArrayList<>();
		chars.addAll(Arrays.asList("同上、否认、无异常、无特殊、看结果、同前、病史同前、病情同前、复查，同初诊".split("、")));
		List<String> permutations = new ArrayList<>();
		generatePermutations(chars, 0, "", permutations);
		permutations.remove("");
		List<String> collect = permutations.stream().distinct().collect(Collectors.toList());
		System.out.println(collect);

	}

	private static void generatePermutations(List<String> chars, int index, String current, List<String> result) {
		if (index == chars.size()) {
			result.add(current);
			return;
		}

		// 选择当前字符，然后递归调用生成剩余字符的所有排列
		generatePermutations(chars, index + 1, current + chars.get(index), result);

		// 不选择当前字符，相当于跳过这个字符，直接处理下一个字符
		generatePermutations(chars, index + 1, current, result);
	}

	private static void permute(List<String> strings, int start, List<List<String>> result) {
		if (start == strings.size() - 1) {
			// 当到达最后一个元素时，添加当前排列到结果列表
			result.add(new ArrayList<>(strings));
		} else {
			for (int i = start; i < strings.size(); i++) {
				// 交换位置以生成新的排列
				Collections.swap(strings, start, i);
				permute(strings, start + 1, result);
				// 回溯，恢复原始顺序
				Collections.swap(strings, start, i);
			}
		}
	}

}
