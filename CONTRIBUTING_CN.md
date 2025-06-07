## Contributor Guide

> 我们致力于维护一个快乐、互相帮助的社区。欢迎每位贡献者加入社区，共建 A2AP 项目，探索 AI 的无限可能！

### 贡献方式

> 在 A2AP 社区中，有许多方式可以参与贡献：

- 💻**代码**：可以帮助社区完成一些任务，编写新功能或提出 bug fix；
- ⚠️**测试**：可以参与编写测试代码，包括单元测试、集成测试、E2E 测试等；
- 📖**文档**：可以撰写或改进文档，帮助用户更好地理解和使用 A2A4J；
- 📝**博客**：可以撰写关于 A2A4J 的文章，帮助社区更好地进行宣传；
- 🤔**讨论**：可以参与 A2A4J 新功能的讨论，将您的想法与 A2A4J 整合；
- 💡**宣传**：可以在会议或峰会上发言，帮助宣传或推广 A2A4J 社区；
- 💬**建议**：您也可以对项目或社区提出一些建议，以促进 A2A4J 健康发展；

更多见 [贡献类型](https://allcontributors.org/docs/en/emoji-key)

即使是小的拼写错误修正也非常欢迎 :)

### 查找任务

找到您感兴趣的问题！在我们的 GitHub 仓库问题列表中，我们通常会发布一些带有标签 `good first issue` 或 `status: volunteer wanted` 的问题。  
这些问题欢迎贡献者领取并为止贡献。其中，`good first issues` 通常门槛较低，适合新人。

当然，如果您有好的想法，也可以直接在 GitHub Discussion 中提议或与社区联系。

### 提交 Pull Request

1. 首先，您需要 fork 目标 [仓库](https://github.com/a2ap/a2a4j)。
2. 然后用 git 命令下载代码到本地：
    ```shell
    git clone git@github.com:${YOUR_USERNAME}/a2a4j.git #推荐  
    ```
3. 下载完成后，请参阅目标仓库的入门指南或 README 文件初始化项目。
4. 然后，您可以参考以下命令提交代码：
    ```shell
    git checkout -b a-feature-branch #推荐  
    ```
5. 提交代码时，commit message 要求符合格式规范：[模块名称或类型名称]feature 或 bugfix 或 doc: 提交说明信息。
    ```shell
    git add <修改的文件/路径> 
    git commit -m '[docs]feature: 必要的说明' #推荐 
    ```
6. 推送到远程仓库
    ```shell
    git push origin a-feature-branch   
    ```
7. 然后您可以在 GitHub 上发起新的 PR（Pull Request）。

请注意，PR 的标题需要符合我们社区的规范，并在 PR 中写必要的描述，以便于 Committers 和其他贡献者进行 code review。

### 等待代码合并

提交 PR 后，PMC Member 或 Committers 以及社区的朋友将审查您提交的代码（代码审查），并提出一些修改建议或进行一些讨论。请及时关注您的 PR。

如果后续需要进行更改，无需发起新的 PR。在原始分支上提交一个提交并推送到远程仓库后，PR 将自动更新。

此外，我们的项目有相对规范和严格的 CI 检查流程。提交 PR 后，CI 将被触发。请注意是否通过 CI 检查。

最后，Committers 可以将 PR 合并到主分支中。

### 代码合并后

代码合并后，您可以在本地和远程仓库删除开发分支：

```shell
git branch -d a-dev-branch
git push origin --delete a-dev-branch
```

在主分支上，您可以执行以下操作以同步上游仓库：

```shell
git remote add upstream https://github.com/a2ap/a2a4j.git #绑定远程仓库，如果已经执行过，则不需要再次执行
git checkout master 
git pull upstream master
```

<br>
