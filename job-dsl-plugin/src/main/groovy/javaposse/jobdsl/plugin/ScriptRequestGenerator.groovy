package javaposse.jobdsl.plugin

import com.google.common.collect.Sets
import hudson.EnvVars
import hudson.FilePath
import hudson.model.AbstractBuild
import javaposse.jobdsl.dsl.ScriptRequest

class ScriptRequestGenerator {

    final AbstractBuild build
    EnvVars env

    ScriptRequestGenerator(AbstractBuild build, EnvVars env) {
        this.build = build
        this.env = env
    }

    public Set<ScriptRequest> getScriptRequests(String targets, boolean usingScriptText, String scriptText, boolean ignoreExisting) throws IOException, InterruptedException {
        Set<ScriptRequest> scriptRequests = Sets.newHashSet();

        String jobName = build.getProject().getName();
        URL workspaceUrl = new URL(null, "workspace://" + jobName + "/", new WorkspaceUrlHandler());

        if(usingScriptText) {
            ScriptRequest request = new ScriptRequest(null, scriptText, workspaceUrl, ignoreExisting);
            scriptRequests.add(request);
        } else {
            String targetsStr = env.expand(targets);

            FilePath[] filePaths =  build.getWorkspace().list(targetsStr.replace("\n", ","));
            for (FilePath filePath : filePaths) {
                String relativePath = filePath.parent.getRemote() - build.getWorkspace().getRemote()
                URL relativeWorkspaceUrl = new URL(workspaceUrl, relativePath + "/")
                ScriptRequest request = new ScriptRequest(filePath.name, null, relativeWorkspaceUrl, ignoreExisting);
                scriptRequests.add(request);
            }
        }
        return scriptRequests;
    }
}
