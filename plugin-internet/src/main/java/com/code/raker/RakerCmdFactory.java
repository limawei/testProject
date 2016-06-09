package com.code.raker;

import java.util.HashMap;
import java.util.Map;

import com.code.raker.cmd.CmdDownload;
import com.code.raker.cmd.CmdFindClue;
import com.code.raker.cmd.CmdLogin;

public class RakerCmdFactory {

	private Map<String, Class<? extends RakerCmd>> classMap = new HashMap<String, Class<? extends RakerCmd>>();

	private Map<String, RakerCmd> objectMap = new HashMap<String, RakerCmd>();


	public static RakerCmd create(RakerCmd.CMD key) {
		RakerCmd cmd = null;
		if (key == RakerCmd.CMD.DOWNLOAD) {
			cmd = new CmdDownload();
		} else if (key == RakerCmd.CMD.FIND_CLUE) {
			cmd = new CmdFindClue();
		} else if (key == RakerCmd.CMD.LOGIN) {
			cmd = new CmdLogin();
		} else {
			throw new IllegalArgumentException("无此命令");
		}
		return cmd;

	}
}
