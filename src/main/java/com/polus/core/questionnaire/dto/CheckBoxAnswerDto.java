package com.polus.core.questionnaire.dto;

public class CheckBoxAnswerDto {

	private String optionName;
	private Boolean selectedOption;
	
	public String getOptionName() {
		return optionName;
	}
	public void setOptionName(String optionName) {
		this.optionName = optionName;
	}
	public Boolean getSelectedOption() {
		return selectedOption;
	}
	public void setSelectedOption(Boolean selectedOption) {
		this.selectedOption = selectedOption;
	}

}
