package org.dinghuang.activiti.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.dinghuang.activiti.dto.TaskDTO;
import org.dinghuang.activiti.util.ActivitiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedList;
import java.util.List;

/**
 * @author dinghuang123@gmail.com
 * @since 2020/3/3
 */
@Controller
@RequestMapping(value = "/v1/activiti")
@Api(value = "工作流", tags = {"工作流"})
public class ActivitiController {

    @Autowired
    private ActivitiUtils activitiUtils;

    @PostMapping(value = "/start")
    @ApiOperation(value = "启动实例流程")
    public ResponseEntity<String> start(@ApiParam(value = "xml中定义的流程id,这里是dinghuangTest", required = true)
                                        @RequestParam String processId) {
        org.activiti.api.process.model.ProcessInstance processInstance = activitiUtils.startProcessInstance(processId, processId, null);
        return new ResponseEntity<>(processInstance.getId(), HttpStatus.CREATED);
    }

    @GetMapping(value = "/task_list")
    @ApiOperation(value = "根据用户名称查询任务列表")
    public ResponseEntity<List<TaskDTO>> taskList(@ApiParam(value = "用户名称", required = true)
                                                  @RequestParam String userName) {
        List<TaskDTO> taskDTOS = new LinkedList<>();
        activitiUtils.queryTaskList(userName).forEach(task -> {
            TaskDTO taskDTO = new TaskDTO();
            taskDTO.setAssignee(task.getAssignee());
            taskDTO.setId(task.getId());
            taskDTO.setName(task.getName());
            taskDTO.setDescription(task.getDescription());
            taskDTOS.add(taskDTO);
        });
        return new ResponseEntity<>(taskDTOS, HttpStatus.OK);
    }

    @PostMapping(value = "/approve")
    @ApiOperation(value = "完成任务")
    public ResponseEntity<Boolean> managerApprove(@ApiParam(value = "taskId", required = true)
                                                  @RequestParam String taskId) {
        activitiUtils.completeTask(taskId, null);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @PostMapping(value = "/back")
    @ApiOperation(value = "撤回")
    public ResponseEntity<Boolean> back(@ApiParam(value = "taskId", required = true)
                                        @RequestParam String taskId,
                                        @ApiParam(value = "processInstanceId", required = true)
                                        @RequestParam String processInstanceId) {
        activitiUtils.backTask(taskId, processInstanceId);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @PostMapping(value = "/back2")
    @ApiOperation(value = "撤回2")
    public ResponseEntity<Boolean> backTwo(@ApiParam(value = "taskId", required = true)
                                           @RequestParam String taskId,
                                           @ApiParam(value = "targetTaskId", required = true)
                                           @RequestParam String targetTaskId) {
        activitiUtils.backTwo(taskId, targetTaskId);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @GetMapping(value = "/show_img")
    @ApiOperation(value = "查看当前流程图")
    public void showImg(@ApiParam(value = "xml中定义的实例id", required = true)
                        @RequestParam(required = false) String instanceKey, HttpServletResponse response) {
        activitiUtils.showImg(instanceKey, response);
    }

    @ApiOperation(value = "跳转到测试主页面")
    @GetMapping(value = "/index")
    public ModelAndView index(HttpServletRequest req) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("/index.html");
        return mv;
    }

}
