/**
 * Copyright (c) 2018, vindell (https://github.com/vindell).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package net.jeebiz.ftpclient.metrics;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

public class FTPClientMetricsFactory {
	
	protected static final MetricRegistry DEFAULT_REGISTRY = new MetricRegistry();
 
	public static MetricRegistry getMetricRegistry() {
		return DEFAULT_REGISTRY;
	}
	
	public static <T> Histogram getHistogram(Class<T> clazz, String... names) {
		return DEFAULT_REGISTRY.histogram(MetricRegistry.name(clazz.getName(), names));
	}
	
	public static <T> Timer getTimer(Class<T> clazz, String... names) {
		return DEFAULT_REGISTRY.timer(MetricRegistry.name(clazz.getName(), names));
	}
	
	public static <T> Meter getMeter(Class<T> clazz, String... names) {
		return DEFAULT_REGISTRY.meter(MetricRegistry.name(clazz.getName(), names));
	}
	
	public static <T> Counter getCounter(Class<T> clazz, String... names) {
		return DEFAULT_REGISTRY.counter(MetricRegistry.name(clazz.getName(), names));
	}
	
}
