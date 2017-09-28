package com.xedrux.cclouds.models;

public class Language {

	public String code;
	public String flag;
	public String name;
	public String nameEnglish;

	public Language(String code, String flag, String name, String nameEnglish) {
		this.code = code;
		this.flag = flag;
		this.name = name;
		this.nameEnglish = nameEnglish;
	}
	public Language()
	{

	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNameEnglish() {
		return nameEnglish;
	}

	public void setNameEnglish(String nameEnglish) {
		this.nameEnglish = nameEnglish;
	}
}
