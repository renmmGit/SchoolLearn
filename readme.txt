1 �����github�л�ȡ��ĿȨ�ޣ�
�� ����git
git config --global user.name "XXX"
git config --global user.email "XXX@XXX"

�� ������Կ��
ssh-keygen -t rsa -C "���������"
����������λس�
cd ~/.ssh
pwd
��������ָ��ص�·����id_rsa.pub�е��ַ�������Ա����Ȩ��




2 gitbashָ��
��1����һ�δ�github�ϻ�ȡ���룺
	git clone git@github.com:perpohou/helloWorld.git

��2���Ժ��ȡ
	git pull git@github.com:perpohou/helloWorld.git

��ע������ʾNot a git repository (or any of the parent directories): .git˵�����ذ汾����ֿⱻɾ���ˣ���Ҫ���³�ʼ���ֿ⣬�����µĲֿ⣺git init����git pull git@github.com:perpohou/helloWorld.git���ɣ�

��3���ϴ��ļ�����git hub
	��git add .  //���������ļ���ӵ��ֿ�,����ڵ�ǰ·����,�ɲ���·��ֱ�ӽ������ļ��ϴ�;���ڵ�ǰ·����,��./url��
	��git commit -m "��������" 
	��git push origin master

�ϴ�ָ�
1��git init
2��git add �ļ�����
3��git commit -m "�ļ�˵��"
4��git remote add origin git@github.com:renmmGit/SchoolLearn.git 
5��git push -u origin master�����������ڲ���5ǰ��һ��ָ�git pull --rebase origin master�� 

��4�����˴�����Ϊ����ǰ�����һ��
	git reset --hard Head
	git reset --hard +������µİ汾�������ɻ������ð汾

��5��ɾ�����ϴ����ļ�
git rm --cached -r useless
git commit -m "remove directory from remote repository"
git push

��6������ָ��
	git log --�г����и��µİ汾

