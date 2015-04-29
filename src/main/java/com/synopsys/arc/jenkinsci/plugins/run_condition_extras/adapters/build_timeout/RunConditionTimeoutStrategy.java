/*
 * The MIT License
 *
 * Copyright 2013 Oleg Nenashev <nenashev@synopsys.com>, Synopsys Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.synopsys.arc.jenkinsci.plugins.run_condition_extras.adapters.build_timeout;

import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.Descriptor;
import hudson.model.Run;
import hudson.plugins.build_timeout.BuildTimeOutStrategy;
import hudson.plugins.build_timeout.BuildTimeOutStrategyDescriptor;
import java.util.ArrayList;
import java.util.List;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Timeout Strategy, which selects timeout according to run conditions.
 * @author Oleg Nenashev <nenashev@synopsys.com>, Synopsys Inc.
 * @since 0.1
 */
public class RunConditionTimeoutStrategy extends BuildTimeOutStrategy  {
    private static final List<ConditionalTimeout> EMPTY = new ArrayList<ConditionalTimeout>();
    private List<ConditionalTimeout> conditions;
    private long defaultTimeout;

    @DataBoundConstructor
    public RunConditionTimeoutStrategy(List<ConditionalTimeout> conditions, long defaultTimeout) {
        this.conditions = conditions != null ? conditions : EMPTY;
        this.defaultTimeout = defaultTimeout;
    }

    public List<ConditionalTimeout> getConditions() {
        return conditions;
    }

    /**
     * Gets default timeout. 
     * This timeout will be used if all run conditions return false.
     * @return Default timeout
     */
    public long getDefaultTimeout() {
        return defaultTimeout;
    }

    @Override
    public long getTimeOut(Run run) {
        long timeout = defaultTimeout;
        
        if (run instanceof AbstractBuild) {
            AbstractBuild build = (AbstractBuild) run;
            for (ConditionalTimeout condition : conditions) {
                if (condition.isApplicable(build)) {
                    timeout = condition.getTimeout();
                    break;
                }
            }
        } else {
            // Log class cast error
            Utils.logError(Messages.RunConditionTimeoutStrategy_classConvError(
                    run.getClass().getCanonicalName()));
        }
        
        Utils.logMessage(Messages.RunConditionTimeoutStrategy_timeoutIs(Long.toString(timeout)));               
        return timeout;
    }

    @Override
    public Descriptor<BuildTimeOutStrategy> getDescriptor() {
        return DESCRIPTOR;
    }
    
    @Extension(optional = true)
    public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();
    
    public static class DescriptorImpl extends BuildTimeOutStrategyDescriptor {

        @Override
        public String getDisplayName() {
            return Messages.RunConditionTimeoutStrategy_displayName();
        }      
    } 
}
