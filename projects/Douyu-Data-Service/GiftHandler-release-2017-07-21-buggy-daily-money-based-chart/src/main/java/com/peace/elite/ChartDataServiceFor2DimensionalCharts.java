package com.peace.elite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import com.peace.elite.GiftHandlerApplication.ReceivingEventFactory;
import com.peace.elite.chartService.DailyMoneyBasedChart;
import com.peace.elite.chartService.chartData.ChartData;
import com.peace.elite.chartService.entity.ChartUpdateData2D;
import com.peace.elite.chartService.entity.ExtendedCharEntry2D;
import com.peace.elite.entities.ChartEntry2D;
import com.peace.elite.entities.Giving;
import com.peace.elite.eventListener.Event;
import com.peace.elite.eventListener.EventFactory;
import com.peace.elite.eventListener.Listener;
import com.peace.elite.partition.Partitions2D;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Synchronized;

@Data
public class ChartDataServiceFor2DimensionalCharts extends ChartData<ChartEntry2D, ChartEntry2D> {

	private EventFactory<ChartEntry2D> partitions;
	@Autowired
	DailyMoneyBasedChart dailyMoneyBasedChart;
	
	public ChartDataServiceFor2DimensionalCharts(EventFactory<ChartEntry2D> partitions2d, String publishChannel,
			SimpMessagingTemplate webSocket) {
		this.partitions = partitions2d;
		partitions.register(this);
		WEB_SOCKET_PUBLISH_CHANNEL = publishChannel;
		this.webSocket = webSocket;
	}

	@Override
	public ChartEntry2D clone(ChartEntry2D e) {
		return e.clone();
	}

	@Override
	public void publish(ChartEntry2D e1, ChartEntry2D e2) {
		// TODO Auto-generated method stub
		if (e2 != null) {
			publish(new Event<>(e2));
			if (e1 == null) {

			} else {
				long oldData = e1.getData();
				e1.setData((-1) * oldData);
				publish(new Event<>(e1));
			}
		}
	}

	public static ChartData2D getChartData(List<ChartEntry2D> data) {
		return new ChartData2D(
				data.stream().filter(e -> e != null).map(e -> e.getLabel()).collect(Collectors.toList())
						.toArray(new String[data.size()]),
				data.stream().filter(e -> e != null).map(e -> e.getData()).collect(Collectors.toList())
						.toArray(new Long[data.size()]),
				data.stream().filter(e -> e != null).map(e -> e.getColor()).collect(Collectors.toList())
						.toArray(new String[data.size()]));

	}

	@Override
	public ChartData2D getChartData() {
		// TODO Auto-generated method stub
		List<ChartEntry2D> d = Arrays.asList(chartEntries.toArray(new ChartEntry2D[chartEntries.size()]));
		//Collections.sort(d);
		return getChartData(d);
	}

	@Override
	// @Synchronized
	public void webSocketUpdate(int index, ChartEntry2D e) {
		// TODO Auto-generated method stub
		ChartUpdateData2D updateData;
		updateData = new ChartUpdateData2D(index, e.getLabel(), e.getData(), e.getColor());
		webSocket.convertAndSend(WEB_SOCKET_PUBLISH_CHANNEL + "/update", updateData);
	}

	public void useAsDataSource() {
		for (ChartEntry2D e : chartEntries) {
			publish(new Event<>(e));
		}
	}

	@Override
	public ChartData2D getChartDataReverse() {
		// TODO Auto-generated method stub
		List<ChartEntry2D> d = Arrays.asList(chartEntries.toArray(new ChartEntry2D[chartEntries.size()]));
		//Collections.sort(d);
		Collections.reverse(d);
		return getChartData(d);
	}

	@Override
	public ChartData2D getChartDataCustomOrder(Comparator<ChartEntry2D> comparator) {
		// TODO Auto-generated method stub
		List<ChartEntry2D> d = Arrays.asList(chartEntries.toArray(new ChartEntry2D[chartEntries.size()]));
		Collections.sort(d, comparator);
		Collections.reverse(d);
		return getChartData(d);
	}

	@Override

	public void updateAndPublish(ChartEntry2D entry) {
		int index = -1;
		if (handlers.size() == 0) {
			if (entry.getData() >= 0) {
				for (ChartEntry2D e : chartEntries) {
					if (e.equals(entry)) {
						index = Arrays.binarySearch(chartEntries.toArray(), entry);
						// minus the first one, and leave the second one be zero
						// publish(e,e);
						e.setData(entry.getData());
						webSocketUpdate(index, entry);
						return;
					}
				}
				if (index < 0) {
					chartEntries.add(entry);
					index = Arrays.binarySearch(chartEntries.toArray(), entry);
					//重发
					//dailyMoneyBasedChart.reloadChart();
				}

				webSocketUpdate(index, entry);

				return;

			}
		} else {

			publish(new Event<>(entry));
		}
		// ChartEntry2D e1 = null;

		// ChartEntry2D e2 = entry.clone();
		// publish(e1, e2);
	}

}
