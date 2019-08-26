package com.example.esanalytics.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.esanalytics.common.RegisterFund;
import com.example.esanalytics.service.AnalyticService;

@Component
@EnableScheduling
public class Scheduler {
	public static final Logger logger = LoggerFactory.getLogger(Scheduler.class);
	@Autowired
	AnalyticService analyticService;
	
	@Value("${scheduler.cron}")
	private String schedule;
	
	@Scheduled(cron = "${scheduler.cron}" )
	public void runSchedule() {
		String infoString = String.format("Run schedule [%s] ...", schedule);
		logger.info(infoString);
		RegisterFund[] funds = analyticService.getRegisterFunds();
		if (funds != null && funds.length > 0) {
			for (RegisterFund fund : funds) {
				logger.info(String.format("Daily update fund (%s)", fund));
				analyticService.dailyUpdate(fund.getSymbol(), fund.getToken(), true);
			}
		} else {
			logger.info("No fund registers daily update.");
		}
	}
}
