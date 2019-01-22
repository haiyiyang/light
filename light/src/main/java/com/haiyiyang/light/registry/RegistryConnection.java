package com.haiyiyang.light.registry;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.haiyiyang.light.constant.LightConstants;

public abstract class RegistryConnection implements Watcher {

	private static final Logger LOGGER = LoggerFactory.getLogger(RegistryConnection.class);

	protected String registry = null;
	protected ZooKeeper zooKeeper = null;

	private CountDownLatch countDownLatch = null;
	protected static final int SESSION_TIMEOUT = 30 * 1000;

	protected static volatile ConcurrentHashMap<String, String> REGISTRY_LEVEL_LOCK = new ConcurrentHashMap<>(8);

	protected static ConcurrentHashMap<String, ZooKeeper> REGISTRIES = new ConcurrentHashMap<>(8);

	protected RegistryConnection(String registry) {
		synchronized (REGISTRY_LEVEL_LOCK.get(registry)) {
			this.registry = registry;
			this.getRegistry();
		}
	}

	protected static void setRegistryLevelLock(String registry) {
		REGISTRY_LEVEL_LOCK.putIfAbsent(registry, registry.intern());
	}

	@Override
	public void process(WatchedEvent event) {
		if (event.getState() == KeeperState.SyncConnected) {
			if (countDownLatch != null) {
				countDownLatch.countDown();
				LOGGER.info("CountDownLatch has counted down the latch.");
			}
			if (event.getPath() != null) {
				doProcess(false, event);
			}
		} else if (event.getState() == KeeperState.Expired) {
			getRegistry();
			doProcess(true, event);
			LOGGER.info("KeeperState Expired. zooKeeper: {}", zooKeeper);
		}
	}

	abstract public void doProcess(boolean sessionExpired, WatchedEvent event);

	protected ZooKeeper getRegistry() {
		synchronized (registry.intern()) {
			zooKeeper = REGISTRIES.get(registry);
			if (zooKeeper == null) {
				this.connect();
			}
			if (zooKeeper != null) {
				if (!zooKeeper.getState().isAlive()) {
					this.connect();
				} else if (!zooKeeper.getState().isConnected()) {
					this.close();
					this.connect();
				}
			}
			return zooKeeper;
		}
	}

	private void connect() {
		try {
			zooKeeper = new ZooKeeper(registry, SESSION_TIMEOUT, this);
			REGISTRIES.put(registry, zooKeeper);
			countDownLatch = new CountDownLatch(1);
			boolean flag = countDownLatch.await(SESSION_TIMEOUT, TimeUnit.MILLISECONDS);
			LOGGER.info("The value of countDownLatch.await: {}.", flag);
			LOGGER.info("Zookeeper connection {} have been establish.", registry);
		} catch (IOException e) {
			countDownLatch.countDown();
			LOGGER.error("Zookeeper connection {} error: {}.", registry, e.getMessage());
		} catch (InterruptedException e) {
			countDownLatch.countDown();
			LOGGER.error("Zookeeper connection {} got interrupted exception: {}.", registry, e.getMessage());
		}
	}

	private void close() {
		if (zooKeeper != null) {
			try {
				zooKeeper.close();
				LOGGER.info("Closed zookeeper connection.");
			} catch (InterruptedException e) {
				LOGGER.error("Closed zookeeper connection error.");
			}
		}
	}

	private void createLastPath(String path, byte[] data, List<ACL> acl, CreateMode createMode) {
		if (existsPath(path, false) == null) {
			try {
				getRegistry().create(path, data, acl, createMode);
				LOGGER.info("ZookKeeper created the path {}.", path);
			} catch (KeeperException ke) {
				LOGGER.error("Execute createPath {} caused KeeperException error : {}", path, ke.getMessage());
			} catch (InterruptedException ie) {
				LOGGER.error("Execute createPath {} caused InterruptedException error : {}", path, ie.getMessage());
			}
		}
	}

	protected Stat existsPath(String path, boolean watch) {
		try {
			return getRegistry().exists(path, watch);
		} catch (KeeperException ke) {
			LOGGER.error("Execute existsPath caused keeper exception, code : {}", ke.code());
			return null;
		} catch (InterruptedException ie) {
			LOGGER.error("Execute existsPath caused interrupted exception : {}", ie.getMessage());
			return null;
		}
	}

	protected void createServicePath(String fullPath, byte[] data) {
		synchronized (this.registry) {
			String[] pathArray = fullPath.split("/");
			StringBuilder pathStrb = new StringBuilder();
			int i = 1;
			while (i < pathArray.length) {
				pathStrb.append(LightConstants.SLASH).append(pathArray[i]);
				if (++i != pathArray.length) {
					createLastPath(pathStrb.toString(), null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
				} else {
					createLastPath(pathStrb.toString(), data, Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
				}
			}
		}
	}

	public byte[] getData(String path, Watcher watcher) {
		if (existsPath(path, false) != null) {
			try {
				return getRegistry().getData(path, watcher, null);
			} catch (KeeperException ke) {
				LOGGER.error("Execute getData caused KeeperException error : {}", ke.getMessage());
			} catch (InterruptedException ie) {
				LOGGER.error("Execute getData caused InterruptedException error : {}", ie.getMessage());
			}
		}
		return null;
	}

	public List<String> getChildren(String path, Watcher watcher) {
		if (existsPath(path, false) != null) {
			try {
				return getRegistry().getChildren(path, watcher);
			} catch (KeeperException ke) {
				LOGGER.error("Execute getChildren of path {} caused KeeperException error : {}", path, ke.getMessage());
			} catch (InterruptedException ie) {
				LOGGER.error("Execute getChildren of path {} caused InterruptedException error : {}", path,
						ie.getMessage());
			}
		}
		return null;
	}

	public void deleteServicePath(String fullPath) {
		if (existsPath(fullPath, false) == null) {
			LOGGER.info("This path {} does not exists.", fullPath);
			return;
		}
		try {
			getRegistry().delete(fullPath, -1);
			LOGGER.info("Deleted the path {}.", fullPath);
		} catch (InterruptedException ie) {
			LOGGER.error("Execute createServicePath of path {} caused InterruptedException error : {}", fullPath,
					ie.getMessage());
		} catch (KeeperException ke) {
			LOGGER.error("Execute createServicePath of path {} caused KeeperException error : {}", fullPath,
					ke.getMessage());
		}
	}

}
