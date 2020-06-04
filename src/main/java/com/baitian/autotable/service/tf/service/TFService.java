package com.baitian.autotable.service.tf.service;

import com.baitian.autotable.util.CmdUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author ldb
 * @date 2020/5/28 14:25
 */
@Service
public class TFService {
	@Value("${com.baitian.autotable.tf.allLocation}")
	public String allLocation;
	@Value("${com.baitian.autotable.tf.exeLocation}")
	public String exeLocation;

	public boolean getAll() {
		String command = getCommand(allLocation);
		return CmdUtil.executeCommand(command);
	}

	private String getCommand(String location) {
		return exeLocation + "  " + "get" + "  " + location;
	}

}
