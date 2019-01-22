package com.haiyiyang.light.context;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.haiyiyang.light.app.ShutdownHook;
import com.haiyiyang.light.app.props.SettingsProps;
import com.haiyiyang.light.constant.LightConstants;
import com.haiyiyang.light.exception.LightException;
import com.haiyiyang.light.meta.LightAppMeta;
import com.haiyiyang.light.service.LightService;
import com.haiyiyang.light.service.annotation.IAmALightService;

public class LightContext extends AnnotationConfigApplicationContext {

	private static final Logger LOGGER = LoggerFactory.getLogger(LightContext.class);

	private LightAppMeta lightAppMeta;

	private SettingsProps settingsProps;

	private static volatile LightContext LIGHT_CONTEXT;

	private LightContext() {
	}

	public static LightContext getContext() {
		return getContext(null);
	}

	public static LightContext getContext(AbstractApplicationContext ctx) {
		if (LIGHT_CONTEXT != null) {
			return LIGHT_CONTEXT;
		}
		synchronized (LightContext.class) {
			if (LIGHT_CONTEXT == null) {
				LIGHT_CONTEXT = new LightContext();
				LIGHT_CONTEXT.initialize(ctx);
			}
		}
		return LIGHT_CONTEXT;
	}

	public static LightAppMeta getLightAppMeta() {
		return getContext().lightAppMeta;
	}

	private void initialize(AbstractApplicationContext ctx) {
		try {
			settingsProps = SettingsProps.SINGLETON();
			lightAppMeta = LightAppMeta.SINGLETON(settingsProps.getAppName());
			Map<String, Object> objectMap;
			if (ctx != null) {
				this.refresh();
				objectMap = ctx.getBeansWithAnnotation(IAmALightService.class);
			} else {
				loadCompontents();
				objectMap = this.getBeansWithAnnotation(IAmALightService.class);
			}
			if (!lightAppMeta.getLightProps().isDisablePublish()) {
				if (objectMap != null && !objectMap.isEmpty()) {
					LightService.publishLightService(objectMap.values());
				}
			}
		} catch (IOException e) {
			LOGGER.info("Initialization configuration[SettingsProps] failed.");
			throw new LightException(e);
		}
	}

	private void loadCompontents() {
		try {
			String scanPackages = settingsProps.getScanPackages();
			if (scanPackages != null && !scanPackages.isEmpty()) {
				this.scan(scanPackages.split(LightConstants.COMMA));
				LOGGER.info("Scanned packages: {}.", scanPackages);
			}
			Class<?>[] classes = settingsProps.getConfigurableClasses();
			if (classes != null && classes.length > 0) {
				this.register(classes);
				LOGGER.info("Registered packages: {}.", settingsProps.getAnnotatedClasses());
			}
			this.refresh();
		} catch (ClassNotFoundException e) {
			LOGGER.info("Registered annotatedClasses in configuration[SettingsProps] failed.");
			throw new LightException(e);
		}
	}

	@Override
	protected void onClose() {
		LightService.doUnpublishLightService();
	}

	@Override
	public void registerShutdownHook() {
		ShutdownHook.hook();
	}
}