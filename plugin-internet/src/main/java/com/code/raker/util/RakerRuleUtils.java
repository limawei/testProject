package com.code.raker.util;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.selector.Selectable;

import com.code.metadata.raker.EnumAlgorithm;
import com.code.metadata.raker.EnumDataRule;
import com.code.metadata.raker.RakerTextRule;

/**
 * 规则提取工具类
 * @author chenzm
 */
public class RakerRuleUtils {
	private RakerRuleUtils(){}
	
	/**
	 * 获取选择器通过父选择器
	 * @param parent 父选择器
	 * @param exp 表达式
	 * @return
	 */
	public static Selectable getSelectableByParent(RakerTextRule exp, Selectable parent){
		if (StringUtils.isNotBlank(exp.getXpath())) {
			return parent.xpath(exp.getXpath());
		} else if (StringUtils.isNotBlank(exp.getJquery())) {
			return parent.$(exp.getJquery());
		}else if(StringUtils.isNotBlank(exp.getCss())) {
			return parent.css(exp.getCss());
		}else if(StringUtils.isNotBlank(exp.getAlgorithm())){
			return invokeAlgorithm(exp, parent);
		}else {
			throw new IllegalArgumentException(exp.getCode() + "没有配置表达式");
		}
	}
	
	public static String easyValue(RakerTextRule exp, Page page){

		Selectable s = getSelectableFromPage(exp,page);
		return s.toString();
	}
	/**
	 * 获取选择器
	 * @param page
	 * @param exp
	 * @return
	 */
	public static Selectable getSelectableFromPage(RakerTextRule exp, Page page) {
		if (StringUtils.isNotBlank(exp.getXpath())) {
			return page.getHtml().xpath(exp.getXpath());
		} else if (StringUtils.isNotBlank(exp.getJquery())) {
			return page.getHtml().$(exp.getJquery());
		}else if(StringUtils.isNotBlank(exp.getCss())) {
			return page.getHtml().css(exp.getCss());
		}else if(StringUtils.isNotBlank(exp.getAlgorithm())){
			return invokeAlgorithm(exp, page.getHtml().xpath("//"));
		}else {
			throw new IllegalArgumentException(exp.getName() + "规则没有配置表达式");
		}
	}
	
	/**
	 * 提取算法调用
	 * @param exp
	 * @param select
	 * @return
	 */
	public static Selectable invokeAlgorithm(RakerTextRule exp, Selectable select) {
		if(StringUtils.isBlank(exp.getAlgorithm())) {
			throw new IllegalArgumentException(exp.getName() + "规则没有配置提取算法");
		}
		switch(EnumAlgorithm.valueOf(exp.getAlgorithm())) {
		case SMART_CONTENT:
			return select.smartContent();
		default:
			throw new IllegalArgumentException("不支持" + EnumAlgorithm.valueOf(exp.getAlgorithm()).getCode() + "提取算法");	
		}
	}
	
	/**
	 * 文本内容匹配
	 * @param exp
	 * @param content
	 * @return
	 */
	public static boolean matchRule(RakerTextRule exp, String content) {
		if(StringUtils.isBlank(exp.getDataRule()) ||  StringUtils.isBlank(exp.getDataContent())) {
			return true;
		}
		switch(EnumDataRule.valueOf(exp.getDataRule())) {
		case INCLUDE:
			return content.contains(exp.getDataContent());
		case INCLUDE_NUMBER:
			return Pattern.compile("[0-9]+?").matcher(content).find();
		case EXCEPT:
			return !content.contains(exp.getDataContent());
		case EXCEPT_NUMBER:
			return !Pattern.compile("[0-9]+?").matcher(content).find();
		default:
			throw new IllegalArgumentException("不支持" + EnumDataRule.valueOf(exp.getDataRule()).getCode() + "匹配规则");	
		}
	}
	
	/**
	 * 去除网页标签
	 * @param html
	 * @return
	 */
	public static String removeHtmlTag(String html) {
		if(StringUtils.isBlank(html)){
			return "";
		}
		return html.replaceAll("(?is)<.*?>", "").replaceAll("\\s+", "");
	}
}
