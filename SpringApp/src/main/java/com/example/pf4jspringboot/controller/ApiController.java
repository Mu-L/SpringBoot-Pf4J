package com.example.pf4jspringboot.controller;

import com.example.pf4jspringboot.Pf4jSpringBootApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.io.FileUtils;
import org.pf4j.PluginState;
import org.pf4j.PluginWrapper;
import org.pf4j.demo.api.Greeting;
import org.pf4j.demo.api.OrderGenerate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
public class ApiController {
    public static final ObjectMapper MAPPER = new ObjectMapper();

    @GetMapping("/great")
    public String great() {
        List<Greeting> extensions = Pf4jSpringBootApplication.pluginManager.getExtensions(Greeting.class);
        if (extensions == null || extensions.size() == 0) {
            return "No Plugin";
        }
        return extensions.get(0).getGreeting();
    }

    /**
     * 启用 插件
     * */
    @GetMapping("/start")
    public String start(@RequestParam String id) {
        PluginState pluginState = Pf4jSpringBootApplication.pluginManager.startPlugin(id);
        return pluginState.toString();
    }

    /**
     * 禁用 插件
     * */
    @GetMapping("/stop")
    public String stop(@RequestParam String id) {
        PluginState pluginState = Pf4jSpringBootApplication.pluginManager.stopPlugin(id);
        return pluginState.toString();
    }

    @PostMapping("/order")
    public ObjectNode order(@RequestBody ObjectNode orderInfo) {
        ObjectNode result = MAPPER.createObjectNode();
        List<OrderGenerate> orderGenerates = Pf4jSpringBootApplication.pluginManager.getExtensions(OrderGenerate.class);
        if (orderGenerates == null || orderGenerates.size() == 0) {
            result.put("result", false);
            result.put("msg", "没有配置下单插件");
            return result;
        }
        OrderGenerate handler = orderGenerates.get(0);
        try {
            handler.validate(orderInfo);
        } catch (Exception e) {
            result.put("result", false);
            result.put("msg", "异常: " + e.getMessage());
            e.printStackTrace();
            return result;
        }
        ObjectNode orderInfoAfterFix = handler.fix(orderInfo);
        List<String> persistence = handler.persistence(orderInfoAfterFix);
        ArrayNode sqls = MAPPER.createArrayNode();
        for (String s : persistence) {
            sqls.add(s);
        }
        result.put("result", true);
        result.put("msg", "success");
        result.set("data", sqls);
        return result;
    }

    @PostMapping("/great")
    public ObjectNode setGreat(@RequestParam String id, @RequestParam(value = "file") MultipartFile file) {
        ObjectNode result = MAPPER.createObjectNode();
        try {
            String pluginFIleName = id + ".jar";
            System.out.println("开始更新插件: " + pluginFIleName);
            savePluginFile(file, pluginFIleName);
            boolean b = Pf4jSpringBootApplication.pluginManager.deletePlugin(id);
            String newPluginId = Pf4jSpringBootApplication.pluginManager.loadPlugin(Paths.get(Pf4jSpringBootApplication.PLUGIN_PATH, pluginFIleName));
            PluginState pluginState = Pf4jSpringBootApplication.pluginManager.startPlugin(newPluginId);
            result.put("pluginState", pluginState.toString());
            result.put("deletePluginResult", b);
            result.put("newId", newPluginId);
            result.put("result", true);
            System.out.println("插件更新成功");
        } catch (Exception e) {
            result.put("result", false);
            result.put("msg", e.getMessage());
            e.printStackTrace();
            System.out.println("插件更新失败");
        }
        return result;
    }

    @GetMapping("/pluginInfo")
    public ObjectNode info() {
        ObjectNode result = MAPPER.createObjectNode();
        ArrayNode data = MAPPER.createArrayNode();
        List<PluginWrapper> startedPlugins = Pf4jSpringBootApplication.pluginManager.getPlugins();
        for (PluginWrapper plugin : startedPlugins) {
            String pluginId = plugin.getDescriptor().getPluginId();
            System.out.println("Extensions added by plugin " + pluginId);
            Set<String> extensionClassNames = Pf4jSpringBootApplication.pluginManager.getExtensionClassNames(pluginId);
            for (String extension : extensionClassNames) {
                data.add(MAPPER.createObjectNode()
                        .put("id", pluginId)
                        .put("name", extension)
                        .put("status", plugin.getPluginState().toString())
                        .put("version", plugin.getDescriptor().getVersion())
                );
            }
        }
        result.set("data", data);
        return result;
    }

    private void savePluginFile(MultipartFile file, String pluginFIleName) throws IOException {
        File dest = org.apache.commons.io.FileUtils.getFile(Pf4jSpringBootApplication.PLUGIN_PATH, pluginFIleName);
        FileUtils.copyInputStreamToFile(file.getInputStream(), dest);
    }
}
