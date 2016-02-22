package com.cng.cloud.service.impls;

import com.cng.cloud.data.Host;
import com.cng.cloud.service.IHostService;
import org.dreamwork.persistence.GenericServiceSpringImpl;
import org.dreamwork.persistence.Operator;
import org.dreamwork.persistence.Parameter;

/**
 * Created by game on 2016/2/23
 */
public class HostServiceImpl extends GenericServiceSpringImpl<Host, String> implements IHostService {
    @Override
    public boolean existsHost (String mac) {
        return exists (new Parameter ("mac", mac, Operator.EQ));
    }
}
