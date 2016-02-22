package com.cng.cloud.service;

import com.cng.cloud.data.Host;
import org.dreamwork.persistence.IGenericService;

/**
 * Created by game on 2016/2/23
 */
public interface IHostService extends IGenericService <Host, String> {
    boolean existsHost (String mac);
}