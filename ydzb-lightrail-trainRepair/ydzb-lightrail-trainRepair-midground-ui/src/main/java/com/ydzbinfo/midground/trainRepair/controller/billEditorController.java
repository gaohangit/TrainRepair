package com.ydzbinfo.midground.trainRepair.controller;

import com.jxdinfo.hussar.core.base.controller.BaseController;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 控制器
 * 
 * @author 张天可
 * 
 */
@Controller
public class billEditorController extends BaseController {
	// @Value("${ydzb.mainWebHost:http://127.0.0.1:8080}")
	// private String mainWebHost;
	@ApiOperation("记录单编辑器页面")
	@GetMapping("static/sheetsDesigner/index/index.html")
	public String billEditorView(Model model) {
		model.addAttribute("mainweb", "");
		return "/emis/trainRepair/bill/billEditor/index.html";
	}

}
