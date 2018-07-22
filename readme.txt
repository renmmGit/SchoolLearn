1 如何在github中获取项目权限？
① 配置git
git config --global user.name "XXX"
git config --global user.email "XXX@XXX"

② 生成密钥对
ssh-keygen -t rsa -C "上面的邮箱"
连续点击三次回车
cd ~/.ssh
pwd
进入上面指令返回的路径将id_rsa.pub中的字符给管理员开启权限




2 gitbash指令
（1）第一次从github上获取代码：
	git clone git@github.com:perpohou/helloWorld.git

（2）以后获取
	git pull git@github.com:perpohou/helloWorld.git

（注：若显示Not a git repository (or any of the parent directories): .git说明本地版本管理仓库被删除了，需要重新初始化仓库，建立新的仓库：git init，再git pull git@github.com:perpohou/helloWorld.git即可）

（3）上传指令：
	1、git init(只有第一次上传时需要)
	2、git add 文件夹名  （或git add .将所有文件添加到仓库,如果在当前路径下,可不加路径直接将所有文件上传;不在当前路径下,加./url）
	3、git commit -m "文件说明"
	4、git remote add origin git@github.com:renmmGit/SchoolLearn.git （只有第一次上传时需要）
	5、git push -u origin master（如果出错就在步骤5前加一条指令：git pull --rebase origin master） 

（4）回退代码至为更改前的最近一版
	git reset --hard Head
	git reset --hard +任意更新的版本名，即可回退至该版本

（5）删除已上传的文件
	git rm --cached -r useless
	git commit -m "remove directory from remote repository"
	git push

（6）其他指令
	git log --列出所有更新的版本

