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
package org.jenkins_ci.plugins.run_condition.adapters.build_timeout;

import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.Describable;
import hudson.model.Descriptor;
import org.jenkins_ci.plugins.run_condition.RunCondition;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Encapsulates conditional timeout, which is defined by {@link RunCondition}.
 * @author Oleg Nenashev <nenashev@synopsys.com>, Synopsys Inc.
 * @since 0.1
 */
public class ConditionalTimeout implements Describable<ConditionalTimeout> {
    private RunCondition condition;
    private long timeout;

    @DataBoundConstructor
    public ConditionalTimeout(RunCondition condition, long timeout) {
        this.condition = condition;
        this.timeout = timeout;
    }
 
    public RunCondition getCondition() {
        return condition;
    }

    public long getTimeout() {
        return timeout;
    }
    
    public boolean isApplicable(AbstractBuild build) {
        try {
            return condition.runPerform(build, null);
        } catch (Exception ex) {
            //TODO: write to log
            Utils.logError(Messages.ConditionalTimeout_exceptionMsg(ex.getMessage()));
            return false;
        }
    }

    @Override
    public Descriptor<ConditionalTimeout> getDescriptor() {
        return DESCRIPTOR;
    }
    
    @Extension
    public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();
    
    public static class DescriptorImpl extends Descriptor<ConditionalTimeout> {
        
        @Override
        public String getDisplayName() {
            return Messages.ConditionalTimeout_displayName();
        }     
    }
}
