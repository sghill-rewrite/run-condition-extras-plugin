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
package org.jenkins_ci.plugins.run_condition.adapters.mail_ext;

import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.TaskListener;
import hudson.plugins.emailext.plugins.EmailTrigger;
import hudson.plugins.emailext.plugins.EmailTriggerDescriptor;
import org.jenkins_ci.plugins.run_condition.RunCondition;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Allows to use run conditions in Extended Email Plugin.
 * This trigger wraps a run condition expression.
 * @author Oleg Nenashev <nenashev@synopsys.com>, Synopsys Inc.
 * @since 0.1
 */
public class RunConditionEmailTrigger extends EmailTrigger {
    RunCondition condition;
   
    @DataBoundConstructor
    public RunConditionEmailTrigger(boolean sendToList, boolean sendToDevs, boolean sendToRequestor, boolean sendToCulprits, String recipientList,
            String replyTo, String subject, String body, String attachmentsPattern, int attachBuildLog, String contentType, RunCondition condition) {
        super(sendToList, sendToDevs, sendToRequestor, sendToCulprits, recipientList, replyTo, subject, body, attachmentsPattern, attachBuildLog, contentType);
        this.condition = condition;
    }
    
    @Override
    public boolean isPreBuild() {
        return false;
    }
    
    @Override
    public boolean trigger(AbstractBuild<?, ?> ab, TaskListener tl) {
        BuildListener listener;
        if (tl instanceof BuildListener) {
            listener = (BuildListener)tl;
        } else {
            tl.error("[mail-trigger] - Wrong class of task listener. Skipping the trigger");
            logError(tl, Messages.RunConditionEmailTrigger_listenerClassConvError());
            return false;
        }
        
        try {
            return condition.runPerform(ab, listener);
        } catch (Exception ex) {
            logError(listener, Messages.RunConditionEmailTrigger_exceptionMsg() +ex.getMessage());
            return false;
        }
    }

    public RunCondition getCondition() {
        return condition;
    }
    
    @Extension
    public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

    @Override
    public EmailTriggerDescriptor getDescriptor() {
        return DESCRIPTOR;
    }

    public static final class DescriptorImpl extends EmailTriggerDescriptor {
        @Override
        public String getDisplayName() {
            return Messages.RunConditionEmailTrigger_displayName();
        }     
    }
    
    private void logError(TaskListener listener, String message) {
        listener.error(Messages.logPrefix()+message);
    }
}
