package com.peace.elite;

import lombok.Data;

@Data
public class ChartData2D{
	public ChartData2D(){}
	public ChartData2D(String[] labels, Long[] data){
		this.labels = labels;
		this.data = data;
	}
	private String[] labels;
	private Long[] data;
}