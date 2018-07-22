# -*- coding:utf-8 -*-
import os
import tkinter
import tkinter.filedialog
import tkinter.ttk
import glob
import sys
import io
import shutil
import subprocess
import paramiko
import json
import re


FAILED  = 0
SUCCESS = 1

class ReleaseKernel(object):
    def __init__(self,keyWord,version=None,model = None,projectPath=None,buildPath=None,merge=None,select=None):
        self.projectPath = self.R(projectPath)
        self.projectLocalPath = None
        self.projectFactoryLocalPath = None
        self.buildPath = self.R(buildPath)
        self.builderPath = self.R(self.searchPath(buildPath,"IarBuild.exe"))
        if self.searchPath(self.projectPath,"ImgMaker.bat") != None:
            self.projectLocalPath = self.R(self.getFatherPath(self.getFatherPath(self.searchPath(self.projectPath,"ImgMaker.bat"))))
        if self.searchPath(self.projectPath,"factory.exe") != None:    
            self.projectFactoryLocalPath = self.R(self.getFatherPath(self.searchPath(self.projectPath,"factory.exe")))
        self.projectKeyword = keyWord
        self.version = version
        self.model = model
        self.merge = merge
        self.select = select
        self.projectGitHashCode = "None"
        self.releaseHdr = "None"
        self.releaseBin = "None"
        self.releaseOut = "None"
        self.releaseS = "None"
        self.releaseMap = "None"
        self.condition = 0

        dict = {"keyWord":self.projectKeyword,"projectPath":self.projectPath,"buildPath":self.buildPath,"model":self.model,"merge":self.merge,"select":self.select}
        with open(os.path.dirname(os.path.realpath(__file__)) + "\\releaseTool.json","w") as f:
            json.dump(dict,f)

        self.projectPath = self.RR(self.projectPath)
        self.builderPath = self.RR(self.builderPath)
        self.projectLocalPath = self.RR(self.projectLocalPath)
        self.projectFactoryLocalPath = self.RR(self.projectFactoryLocalPath)
        print("==========PATH INIT=======================================")
        print(self.projectPath)
        print(self.builderPath)
        print(self.projectLocalPath)
        print(self.projectFactoryLocalPath)
        print("==========================================================")
        if self.projectPath != None and \
           self.builderPath != None and \
           self.projectLocalPath != None and \
           self.projectFactoryLocalPath != None and \
           self.projectKeyword != "" and \
           self.version != "" and \
           self.model != "":
            self.condition = 1

####################################################################
#
####################################################################
    def getCondition(self):
        return self.condition

####################################################################
#
####################################################################
    def R(self,path):
        path1 = "\\".join(path.split("/"))
        return path1

####################################################################
#
####################################################################
    def RR(self,path):
        path1 = ""
        temp = path.split("\\")
        for i in range(len(temp)):
            if " " in temp[i]:
                if i == len(temp) - 1:
                    temp[i] = "\"" + temp[i] + "\""
                else:
                    temp[i] = "\"" + temp[i] + "\"\\"
            else:
                if i == len(temp) - 1:
                    pass
                else:
                    temp[i] = temp[i] + "\\"
            path1 = path1 + temp[i]
        return path1

####################################################################
#
####################################################################
    def start(self):
        print("**********KERNEL START*************************************")
        if self.getGitVersion() == SUCCESS:
            print("get git version success")
        else:
            print("get git version error")

        if self.merge == 1:
            re = self.codeCompile321()
        else:
            re = self.codeCompile()
        if re == SUCCESS:
            print("compile success")
            if self.select == 1:
                if self.uploadToServer() == SUCCESS:
                    print("upload success")
                else:
                    print("upload error")
            elif self.select == 2:
                print("upload no need")
        else:
            print("compile error")
        print("***********************************************************")
####################################################################
#
####################################################################
    def searchPath(self,path,file):
        for fpaths,dirs,fs in os.walk(path):
            for addpath in dirs:
                pl = glob.iglob(os.path.join(fpaths,addpath) + "\\" + file)
                for p in pl:
                    return p

####################################################################
#
####################################################################
    def getFatherPath(self,path):
        return os.path.abspath(os.path.dirname(path)+os.path.sep+".")

####################################################################
#
####################################################################
    def getGitVersion(self):
        os.chdir(self.projectPath)
        tmp = subprocess.Popen("git reset --hard HEAD",stdout=subprocess.PIPE,stderr=subprocess.PIPE)
        print(tmp.stdout.readlines())
        tempfile = subprocess.Popen("git reflog -1",stdout=subprocess.PIPE,stderr=subprocess.PIPE)
        gitrefloglist = tempfile.stdout.readlines()
        tempHC = gitrefloglist[0].decode("utf-8").split("HEAD@")[0]
        self.projectGitHashCode = tempHC.split(" ")[0]
        print("reflog line  : " + gitrefloglist[0].decode("utf-8"))
        m =  re.search("[0-9a-fA-F]{7}",tempHC.split(" ")[0])
        if m == None:
            self.projectGitHashCode = "None"
            return FAILED
        else:
            self.projectGitHashCode = m.group()
        filePath = self.projectLocalPath + "\\service\\githash\\src\\githash.c"
        fileGitHashC = open(filePath,"r")
        try:
            lineGitHashCText = fileGitHashC.read().splitlines()
        finally:
            fileGitHashC.close()
        tmpLineCText = lineGitHashCText;
        for index in range(len(tmpLineCText)):
            lineGitHashCText[index] = tmpLineCText[index] + "\n"
            if "char Githashcode" in tmpLineCText[index]:
                lineGitHashCText[index] = "char Githashcode[GITHASHLEN+1]={\"" + self.projectGitHashCode + "\"};\n"
        fileGitHashC = open(filePath,"w+")
        try:
            fileGitHashC.writelines(lineGitHashCText)
        finally:
            fileGitHashC.close()
        return SUCCESS

####################################################################
#
####################################################################
    def codeCompile(self):
        #delete ota/ *.bin
        filelist = os.listdir(self.projectFactoryLocalPath)
        for eachFile in filelist:
            m =  re.search(".bin",eachFile)
            if m == None:
                pass
            else:
                filePath = os.path.join(self.projectFactoryLocalPath,eachFile)
                os.remove(filePath)
        #modify model name:
        self.modifyModelName()
        #CodeCompile
        cmd = self.builderPath + " " + os.path.join(self.projectLocalPath,"workspace","Project.ewp") + " -build Debug -log all"
        os.system(cmd)
        cmd = self.builderPath + " " + os.path.join(self.projectPath,"boot","workspace","Project.ewp") + " -build Debug -log all"
        os.system(cmd)
        #change version
        self.modifyFirmwareRev()
        #create directory
        releaseDirectoryPath = self.projectFactoryLocalPath + "\\" + self.projectKeyword + "Firmware_00" + self.version + "_" + self.projectGitHashCode
        if not os.path.exists(releaseDirectoryPath):
            os.mkdir(releaseDirectoryPath)
        #create and copy main
        imgMakerFolderPath = os.path.join(self.projectLocalPath,"image")
        imgMakerFilePath = os.path.join(imgMakerFolderPath,"ImgMaker.bat")
        
        mainHdrFilePath = os.path.join(imgMakerFolderPath,"project_release.hdr")
        mainMapFilePath = os.path.join(imgMakerFolderPath,"project_release.map")
        mainReadmeFilePath = os.path.join(imgMakerFolderPath,"project_release.README")
        mainSFilePath = os.path.join(imgMakerFolderPath,"project_release.S")
        mainOutFilePath = os.path.join(imgMakerFolderPath,"project.out")
        mainBinFilePath = os.path.join(imgMakerFolderPath,"main.bin")
        os.chdir(imgMakerFolderPath)
        os.system(imgMakerFilePath)
        if os.path.exists(mainHdrFilePath):
            newMainHdrFileName = imgMakerFolderPath + "\\" + self.projectKeyword + "Firmware_00" + self.version + "_" + self.projectGitHashCode + ".hdr"
            newMainMapFileName = imgMakerFolderPath + "\\" + self.projectKeyword + "Firmware_00" + self.version + "_" + self.projectGitHashCode + ".map"
            newMainReadmeFileName = imgMakerFolderPath + "\\" + self.projectKeyword + "Firmware_00" + self.version + "_" + self.projectGitHashCode + ".README"
            newMainSFileName = imgMakerFolderPath + "\\" + self.projectKeyword + "Firmware_00" + self.version + "_" + self.projectGitHashCode + ".S"
            newMainOutFileName = imgMakerFolderPath + "\\" + self.projectKeyword + "Firmware_00" + self.version + "_" + self.projectGitHashCode + ".out"
            try:
                os.rename(mainHdrFilePath,newMainHdrFileName)
                os.rename(mainMapFilePath,newMainMapFileName)
                os.rename(mainReadmeFilePath,newMainReadmeFileName)
                os.rename(mainSFilePath,newMainSFileName)
                os.rename(mainOutFilePath,newMainOutFileName)
            except FileExistsError as e:
                pass
            newMainHdrFileNameCopy = releaseDirectoryPath + "\\" + self.projectKeyword + "Firmware_00" + self.version + "_" + self.projectGitHashCode + ".hdr"
            shutil.copyfile(newMainHdrFileName,newMainHdrFileNameCopy)
            self.releaseHdr = newMainHdrFileNameCopy
            newMainSFileNameCopy = releaseDirectoryPath + "\\" + self.projectKeyword + "Firmware_00" + self.version + "_" + self.projectGitHashCode + ".S"
            shutil.copyfile(newMainSFileName,newMainSFileNameCopy)
            self.releaseS = newMainSFileNameCopy
            newMainOutFileNameCopy = releaseDirectoryPath + "\\" + self.projectKeyword + "Firmware_00" + self.version + "_" + self.projectGitHashCode + ".out"
            shutil.copyfile(newMainOutFileName,newMainOutFileNameCopy)
            self.releaseOut = newMainOutFileNameCopy
            newMainMapFileNameCopy = releaseDirectoryPath + "\\" + self.projectKeyword + "Firmware_00" + self.version + "_" + self.projectGitHashCode + ".map"
            shutil.copyfile(newMainMapFileName,newMainMapFileNameCopy)
            self.releaseMap = newMainMapFileNameCopy
            mainBinFileCopy=self.projectFactoryLocalPath + "\\" + "main.bin"
            shutil.copyfile(mainBinFilePath,mainBinFileCopy)
        else:
            print("ERROR: " + mainHdrFilePath + " do not exist")
            return FAILED


        #create and copy boot
        bootFolderPath = os.path.join(self.projectPath,"boot")
        bootBatFilePath = os.path.join(bootFolderPath,"GenBootBin.bat")
        bootBinFilePath = os.path.join(bootFolderPath,"boot.bin")
        os.chdir(bootFolderPath)
        os.system(bootBatFilePath)
        if os.path.exists(bootBinFilePath):
            bootBinFileCopy = self.projectFactoryLocalPath + "\\" + "boot.bin"
            shutil.copyfile(bootBinFilePath,bootBinFileCopy)
        else:
            print("ERROR: " + bootBinFilePath + " do not exist")
            return FAILED

        #create and rename factory bin
        factoryBinFilePath = os.path.join(self.projectFactoryLocalPath,"factory.bin")
        factoryExeFilePath = os.path.join(self.projectFactoryLocalPath,"factory.exe")
        os.chdir(self.projectFactoryLocalPath)
        os.system(factoryExeFilePath)
        if os.path.exists(factoryBinFilePath):
            factoryBinFileCopy = releaseDirectoryPath + "\\" + self.projectKeyword + "Firmware_00" + self.version + "_" + self.projectGitHashCode + ".bin"
            shutil.copyfile(factoryBinFilePath,factoryBinFileCopy)
            self.releaseBin = factoryBinFileCopy
        else:
            print("ERROR: " + factoryBinFilePath + " do not exist")
            return FAILED
        self.releaseDir = releaseDirectoryPath
        return SUCCESS

####################################################################
#
####################################################################
    def codeCompile321(self):
        #delete ota/ *.bin
        filelist = os.listdir(self.projectFactoryLocalPath)
        for eachFile in filelist:
            m =  re.search(".bin",eachFile)
            if m == None:
                pass
            else:
                filePath = os.path.join(self.projectFactoryLocalPath,eachFile)
                os.remove(filePath)
        #modify model name:
        self.modifyModelName()
        #CodeCompile
        cmd = self.builderPath + " " + os.path.join(self.projectLocalPath,"workspace","Project.ewp") + " -build Debug -log all"
        os.system(cmd)
        cmd = self.builderPath + " " + os.path.join(self.projectPath,"boot","workspace","Project.ewp") + " -build Debug -log all"
        os.system(cmd)
        cmd = self.builderPath + " " + os.path.join(self.projectPath,"music","workspace","Project.ewp") + " -build Debug -log all"
        os.system(cmd)
        #change version
        self.modifyFirmwareRev321()
        #create directory
        releaseDirectoryPath = self.projectFactoryLocalPath + "\\" + self.projectKeyword + "Firmware_00" + self.version + "_" + self.projectGitHashCode
        if not os.path.exists(releaseDirectoryPath):
            os.mkdir(releaseDirectoryPath)
        #create and copy main
        imgMakerFolderPath = os.path.join(self.projectLocalPath,"image")
        imgMakerFilePath = os.path.join(imgMakerFolderPath,"ImgMaker.bat")
        mainMapFilePath = os.path.join(imgMakerFolderPath,"project_release.map")
        mainReadmeFilePath = os.path.join(imgMakerFolderPath,"project_release.README")
        mainSFilePath = os.path.join(imgMakerFolderPath,"project_release.S")
        mainOutFilePath = os.path.join(imgMakerFolderPath,"project.out")
        mainBinFilePath = os.path.join(imgMakerFolderPath,"main.bin")
        os.chdir(imgMakerFolderPath)
        os.system(imgMakerFilePath)
        if os.path.exists(mainBinFilePath):
            newMainMapFileName = imgMakerFolderPath + "\\" + self.projectKeyword + "Firmware_00" + self.version + "_" + self.projectGitHashCode + ".map"
            newMainReadmeFileName = imgMakerFolderPath + "\\" + self.projectKeyword + "Firmware_00" + self.version + "_" + self.projectGitHashCode + ".README"
            newMainSFileName = imgMakerFolderPath + "\\" + self.projectKeyword + "Firmware_00" + self.version + "_" + self.projectGitHashCode + ".S"
            newMainOutFileName = imgMakerFolderPath + "\\" + self.projectKeyword + "Firmware_00" + self.version + "_" + self.projectGitHashCode + ".out"
            try:
                os.rename(mainMapFilePath,newMainMapFileName)
                os.rename(mainReadmeFilePath,newMainReadmeFileName)
                os.rename(mainSFilePath,newMainSFileName)
                os.rename(mainOutFilePath,newMainOutFileName)
            except FileExistsError as e:
                pass
            newMainSFileNameCopy = releaseDirectoryPath + "\\" + self.projectKeyword + "Firmware_00" + self.version + "_" + self.projectGitHashCode + ".S"
            shutil.copyfile(newMainSFileName,newMainSFileNameCopy)
            self.releaseS = newMainSFileNameCopy
            newMainOutFileNameCopy = releaseDirectoryPath + "\\" + self.projectKeyword + "Firmware_00" + self.version + "_" + self.projectGitHashCode + ".out"
            shutil.copyfile(newMainOutFileName,newMainOutFileNameCopy)
            self.releaseOut = newMainOutFileNameCopy
            newMainMapFileNameCopy = releaseDirectoryPath + "\\" + self.projectKeyword + "Firmware_00" + self.version + "_" + self.projectGitHashCode + ".map"
            shutil.copyfile(newMainMapFileName,newMainMapFileNameCopy)
            self.releaseMap = newMainMapFileNameCopy
            mainBinFileCopy=self.projectFactoryLocalPath + "\\" + "main.bin"
            shutil.copyfile(mainBinFilePath,mainBinFileCopy)
        else:
            print("ERROR: " + mainBinFilePath + " do not exist")
            return FAILED
        #create and copy boot
        bootFolderPath = os.path.join(self.projectPath,"boot")
        bootBatFilePath = os.path.join(bootFolderPath,"GenBootBin.bat")
        bootBinFilePath = os.path.join(bootFolderPath,"boot.bin")
        os.chdir(bootFolderPath)
        os.system(bootBatFilePath)
        if os.path.exists(bootBinFilePath):
            bootBinFileCopy = self.projectFactoryLocalPath + "\\" + "boot.bin"
            shutil.copyfile(bootBinFilePath,bootBinFileCopy)
        else:
            print("ERROR: " + bootBinFilePath + " do not exist")
            return FAILED
        #create and copy music
        musicFolderPath = os.path.join(self.projectPath,"music")
        musicBatFilePath = os.path.join(musicFolderPath,"GenMusicBin.bat")
        musicBinFilePath = os.path.join(musicFolderPath,"music.bin")
        os.chdir(musicFolderPath)
        os.system(musicBatFilePath)
        if os.path.exists(musicBinFilePath):
            musicBinFileCopy = self.projectFactoryLocalPath + "\\" + "music.bin"
            shutil.copyfile(musicBinFilePath,musicBinFileCopy)
        else:
            print("ERROR: " + musicBinFilePath + " do not exist")
            return FAILED
        #create and rename factory bin
        factoryExeFilePath = os.path.join(self.projectFactoryLocalPath,"music_factory.exe")
        os.chdir(self.projectFactoryLocalPath)
        os.system(factoryExeFilePath)
        factoryBinFilePath = os.path.join(self.projectFactoryLocalPath,"factory.bin")
        if os.path.exists(factoryBinFilePath):
            factoryBinFileCopy = releaseDirectoryPath + "\\" + self.projectKeyword + "Firmware_00" + self.version + "_321_" + self.projectGitHashCode + ".bin"
            shutil.copyfile(factoryBinFilePath,factoryBinFileCopy)
            self.releaseBin = factoryBinFileCopy
        else:
            print("ERROR: " + factoryBinFilePath + " do not exist")
            return FAILED

        #create hdr
        mergeNewMainExePath = os.path.join(self.projectFactoryLocalPath,"merge_new_main.exe")
        imgMakerExeFilePath = os.path.join(self.projectFactoryLocalPath,"ImageMaker.exe")
        crcExePath = os.path.join(self.projectFactoryLocalPath,"crc.exe")
        os.chdir(self.projectFactoryLocalPath)
        os.system(mergeNewMainExePath)
        os.system(imgMakerExeFilePath)
        tempHdrPath = os.path.join(self.projectFactoryLocalPath,"raw_pad_encrypt_hdr.bin")
        if os.path.exists(tempHdrPath):
            newTempHdrName = self.projectFactoryLocalPath + "\\" + "fw.bin"
            try:
                os.rename(tempHdrPath,newTempHdrName)
            except FileExistsError as e:
                pass
        else:
            print("ERROR: " + tempHdrPath + " do not exist")
            return FAILED
        os.system(crcExePath)
        hdrFilePath = os.path.join(self.projectFactoryLocalPath,"fw_crc.bin")
        if os.path.exists(hdrFilePath):
            newHdrFileNameCopy = releaseDirectoryPath + "\\" + self.projectKeyword + "Firmware_00" + self.version + "_321_" + self.projectGitHashCode + ".hdr"
            shutil.copyfile(hdrFilePath,newHdrFileNameCopy)
            self.releaseHdr = newHdrFileNameCopy
        else:
            print("ERROR: " + hdrFilePath + " do not exist")
            return FAILED

        self.releaseDir = releaseDirectoryPath
        return SUCCESS


####################################################################
#
####################################################################
    def modifyFirmwareRev(self):
        #modify config.ini
        revfilePath = os.path.join(self.projectLocalPath,"image","config.ini")
        fileConfigIni = open(revfilePath,"r")
        try:
            lineConfigIni = fileConfigIni.read().splitlines()
        finally:
            fileConfigIni.close()
        for index in range(len(lineConfigIni)):
            lineConfigIni[index] += "\n"
            if "0.0" in lineConfigIni[index]:
                if(len(self.version) >= 2):
                    lineConfigIni[index]="0.0" + self.version[0] + "." + self.version[1] + "\n"
                elif(len(self.version) == 1):
                    lineConfigIni[index]="0.0" + self.version[0] + "\n"
                else:
                    lineConfigIni[index]="0.0" + "\n"
        fileConfigIni = open(revfilePath,"w+")
        try:
            fileConfigIni.writelines(lineConfigIni)
        finally:
            fileConfigIni.close()
        #modify flash.ini
        revfilePath_ = os.path.join(self.projectFactoryLocalPath,"flash.ini")
        fileConfigIni_ = open(revfilePath_,"r")
        try:
            lineConfigIni_ = fileConfigIni_.read().splitlines()
        finally:
            fileConfigIni_.close()
        for index_ in range(len(lineConfigIni_)):
            lineConfigIni_[index_] += "\n"
            if "0.0" in lineConfigIni_[index_]:
                if(len(self.version) >= 2):
                    lineConfigIni_[index_]="0.0" + self.version[0] + "." + self.version[1] + "\n"
                elif(len(self.version) == 1):
                    lineConfigIni_[index_]="0.0" + self.version[0] + "\n"
                else:
                    lineConfigIni_[index_]="0.0" + "\n"
        fileConfigIni_ = open(revfilePath_,"w+")
        try:
            fileConfigIni_.writelines(lineConfigIni_)
        finally:
            fileConfigIni_.close()
####################################################################
#
####################################################################
    def modifyFirmwareRev321(self):
        #modify config.ini
        revfilePath = os.path.join(self.projectFactoryLocalPath,"config.ini")
        fileConfigIni = open(revfilePath,"r")
        try:
            lineConfigIni = fileConfigIni.read().splitlines()
        finally:
            fileConfigIni.close()
        for index in range(len(lineConfigIni)):
            lineConfigIni[index] += "\n"
            if "0.0" in lineConfigIni[index]:
                if(len(self.version) >= 2):
                    lineConfigIni[index]="0.0" + self.version[0] + "." + self.version[1] + "\n"
                elif(len(self.version) == 1):
                    lineConfigIni[index]="0.0" + self.version[0] + "\n"
                else:
                    lineConfigIni[index]="0.0" + "\n"
        fileConfigIni = open(revfilePath,"w+")
        try:
            fileConfigIni.writelines(lineConfigIni)
        finally:
            fileConfigIni.close()
        #modify flash.ini
        revfilePath_ = os.path.join(self.projectFactoryLocalPath,"flash.ini")
        fileConfigIni_ = open(revfilePath_,"r")
        try:
            lineConfigIni_ = fileConfigIni_.read().splitlines()
        finally:
            fileConfigIni_.close()
        for index_ in range(len(lineConfigIni_)):
            lineConfigIni_[index_] += "\n"
            if "0.0" in lineConfigIni_[index_]:
                if(len(self.version) >= 2):
                    lineConfigIni_[index_]="0.0" + self.version[0] + "." + self.version[1] + "\n"
                elif(len(self.version) == 1):
                    lineConfigIni_[index_]="0.0" + self.version[0] + "\n"
                else:
                    lineConfigIni_[index_]="0.0" + "\n"
        fileConfigIni_ = open(revfilePath_,"w+")
        try:
            fileConfigIni_.writelines(lineConfigIni_)
        finally:
            fileConfigIni_.close()
        #modify music_flash.ini
        revfilePath__ = os.path.join(self.projectFactoryLocalPath,"music_flash.ini")
        fileConfigIni__ = open(revfilePath__,"r")
        try:
            lineConfigIni__ = fileConfigIni__.read().splitlines()
        finally:
            fileConfigIni__.close()
        for index__ in range(len(lineConfigIni__)):
            lineConfigIni__[index__] += "\n"
            if "0.0" in lineConfigIni__[index__]:
                if(len(self.version) >= 2):
                    lineConfigIni__[index__]="0.0" + self.version[0] + "." + self.version[1] + "\n"
                elif(len(self.version) == 1):
                    lineConfigIni__[index__]="0.0" + self.version[0] + "\n"
                else:
                    lineConfigIni__[index__]="0.0" + "\n"
        fileConfigIni__ = open(revfilePath__,"w+")
        try:
            fileConfigIni__.writelines(lineConfigIni__)
        finally:
            fileConfigIni__.close()

####################################################################
#
####################################################################
    def modifyModelName(self):
        filePath = os.path.join(self.projectLocalPath,"service","WiFi","inc","wifi_ctl.h")
        file = open(filePath,"r")
        try:
            lineConfigIni = file.read().splitlines()
        finally:
            file.close()
        for index in range(len(lineConfigIni)):
            lineConfigIni[index] += "\n"
            if ("MODEL_PHILIPS" in lineConfigIni[index]) and ("//#define" not in lineConfigIni[index]):
                lineConfigIni[index]="#define MODEL_PHILIPS \"model philips.light." + self.model + "\\r\"" + "\n"
        file = open(filePath,"w+")
        try:
            file.writelines(lineConfigIni)
        finally:
            file.close()

####################################################################
#
####################################################################
    def uploadToServer(self):
        hostname = "120.132.54.19"
        username = "web"
        password = "yixin"
        port = 22
        remote_dir1 = None
        remote_dir2 = None
        remote_dir3 = None
        remote_dir4 = None
        remote_dir5 = None

        if self.merge == 1:
            remote_dir1 = "/home/web/firmware/" + self.projectKeyword + "/" + self.projectKeyword + "Firmware_00" + self.version + "_321_" + self.projectGitHashCode + ".hdr"
            remote_dir2 = "/home/web/firmware/" + self.projectKeyword + "/" + self.projectKeyword + "Firmware_00" + self.version + "_321_" + self.projectGitHashCode + ".bin"
            remote_dir3 = "/home/web/firmware/" + self.projectKeyword + "/" + self.projectKeyword + "Firmware_00" + self.version + "_321_" + self.projectGitHashCode + ".out"
            remote_dir4 = "/home/web/firmware/" + self.projectKeyword + "/" + self.projectKeyword + "Firmware_00" + self.version + "_321_" + self.projectGitHashCode + ".S"
            remote_dir5 = "/home/web/firmware/" + self.projectKeyword + "/" + self.projectKeyword + "Firmware_00" + self.version + "_321_" + self.projectGitHashCode + ".map"
        else:
            remote_dir1 = "/home/web/firmware/" + self.projectKeyword + "/" + self.projectKeyword + "Firmware_00" + self.version + "_" + self.projectGitHashCode + ".hdr"
            remote_dir2 = "/home/web/firmware/" + self.projectKeyword + "/" + self.projectKeyword + "Firmware_00" + self.version + "_" + self.projectGitHashCode + ".bin"
            remote_dir3 = "/home/web/firmware/" + self.projectKeyword + "/" + self.projectKeyword + "Firmware_00" + self.version + "_" + self.projectGitHashCode + ".out"
            remote_dir4 = "/home/web/firmware/" + self.projectKeyword + "/" + self.projectKeyword + "Firmware_00" + self.version + "_" + self.projectGitHashCode + ".S"
            remote_dir5 = "/home/web/firmware/" + self.projectKeyword + "/" + self.projectKeyword + "Firmware_00" + self.version + "_" + self.projectGitHashCode + ".map"
        # sock=paramiko.ProxyCommand("180.166.223.108:10015")
        # socks.setdefaultproxy(socks.PROXY_TYPE_SOCKS5,"1180.166.223.108",10015)
        # socket.socket =socks.socksocket
        os.environ["https_proxy"] = "180.166.223.108:10015"
        try:
            sftpHandle=paramiko.Transport((hostname,port))
            sftpHandle.connect(username=username,password=password)
            sftp = paramiko.SFTPClient.from_transport(sftpHandle)
            sftp.put(self.releaseHdr,remote_dir1)
            sftp.put(self.releaseBin,remote_dir2)
            sftp.put(self.releaseOut,remote_dir3)
            sftp.put(self.releaseS,remote_dir4)
            sftp.put(self.releaseMap,remote_dir5)
        except Exception:
            print("ERROR: sftp connect error!")
            return FAILED
        finally:
            sftpHandle.close()
        return SUCCESS


class ReleaseGUI(object):
####################################################################
#
####################################################################
    def __init__(self):
        self.top = tkinter.Tk()
        self.top.geometry("400x300")
        self.top.title("程序发布工具")
        self.keyWord = tkinter.StringVar(self.top)
        self.version = tkinter.StringVar(self.top)
        self.model = tkinter.StringVar(self.top)
        self.projectPath = tkinter.StringVar(self.top)
        self.buildPath = tkinter.StringVar(self.top)
        self.merge = tkinter.IntVar(self.top)
        self.select = tkinter.IntVar(self.top)
        initfile = os.path.dirname(os.path.realpath(__file__)) + "\\releaseTool.json"
        if os.path.exists(initfile):
            with open(initfile,"r") as load_f:
                load_dict = json.load(load_f)
                self.keyWord.set(load_dict["keyWord"])
                self.model.set(load_dict["model"])
                self.projectPath.set(load_dict["projectPath"])
                self.buildPath.set(load_dict["buildPath"])
                self.merge.set(load_dict["merge"])
                self.select.set(load_dict["select"])
        else:
            self.keyWord.set("")
            self.model.set("")
            self.projectPath.set("")
            self.buildPath.set("")
            self.merge.set(1)
            self.select.set(1)
        self.version.set("")
            

        #PROJECT AREA:
        self.kfm = tkinter.Frame(self.top)
        self.klabel = tkinter.Label(self.kfm,text="项目选择   : ")
        self.klabel.pack(side=tkinter.LEFT)
        self.kcombobox = tkinter.ttk.Combobox(self.kfm,textvariable=self.keyWord,state="readonly")
        self.kcombobox["values"] = ("bedside","ceiling")
        self.kcombobox.pack(side=tkinter.LEFT)
        self.kfm.pack(pady=5,side=tkinter.TOP)

        #VERSION AREA:
        self.vfm = tkinter.Frame(self.top)
        self.vlabel = tkinter.Label(self.vfm,text="版本号  : ")
        self.vlabel.pack(side=tkinter.LEFT)
        self.ventry = tkinter.Entry(self.vfm,textvariable=self.version)
        self.ventry.pack(side=tkinter.LEFT)
        self.vfm.pack(pady=5,side=tkinter.TOP)

        #MODEL AREA:
        self.mfm = tkinter.Frame(self.top)
        self.mlabel = tkinter.Label(self.mfm,text="模组名  : ")
        self.mlabel.pack(side=tkinter.LEFT)
        self.mentry = tkinter.Entry(self.mfm,textvariable=self.model)
        self.mentry.pack(side=tkinter.LEFT)
        self.mfm.pack(pady=5,side=tkinter.TOP)

        #PATH AREA:
        self.pfm1 = tkinter.Frame(self.top)
        self.plabel1 = tkinter.Label(self.pfm1,text="源代码路径 : ")
        self.plabel1.pack(side=tkinter.LEFT)
        self.pentry1 = tkinter.Entry(self.pfm1,textvariable=self.projectPath)
        self.pentry1.pack(side=tkinter.LEFT)
        self.pbutton1 = tkinter.Button(self.pfm1,bitmap="gray50",text="...      ",command=self.inputProjectPath,width=13, height=13, compound=tkinter.LEFT)
        self.pbutton1.pack(side=tkinter.LEFT)
        self.pfm1.pack(pady=5,side=tkinter.TOP)

        self.pfm2 = tkinter.Frame(self.top)
        self.plabel2 = tkinter.Label(self.pfm2,text="编译器路径 : ")
        self.plabel2.pack(side=tkinter.LEFT)
        self.pentry2 = tkinter.Entry(self.pfm2,textvariable=self.buildPath)
        self.pentry2.pack(side=tkinter.LEFT)
        self.pbutton2 = tkinter.Button(self.pfm2,bitmap="gray50",text="...      ",command=self.inputBuilderPath,width=13, height=13, compound=tkinter.LEFT)
        self.pbutton2.pack(side=tkinter.LEFT)
        self.pfm2.pack(pady=5,side=tkinter.TOP)

        #MERGE AREA:
        self.mgfm = tkinter.Frame(self.top)
        self.mgradiobutton1 = tkinter.Radiobutton(self.mgfm,text="声光",variable=self.merge,value=1)
        self.mgradiobutton1.pack(side=tkinter.LEFT)
        self.mgradiobutton2 = tkinter.Radiobutton(self.mgfm,text="原版",variable=self.merge,value=2)
        self.mgradiobutton2.pack(side=tkinter.LEFT)
        self.mgfm.pack(pady=1,side=tkinter.TOP)

        #SELECT AREA:
        self.sfm = tkinter.Frame(self.top)
        self.sradiobutton1 = tkinter.Radiobutton(self.sfm,text="上传",variable=self.select,value=1)
        self.sradiobutton1.pack(side=tkinter.LEFT)
        self.sradiobutton2 = tkinter.Radiobutton(self.sfm,text="不传",variable=self.select,value=2)
        self.sradiobutton2.pack(side=tkinter.LEFT)
        self.sfm.pack(pady=1,side=tkinter.TOP)

        #BUTTON AREA:
        self.bfm = tkinter.Frame(self.top)
        self.bent = tkinter.Button(self.bfm,text="开始",command=self.start,width=6, height=1)#,background="black",foreground="white"
        self.bent.pack(padx=20,side=tkinter.LEFT)
        self.bqut = tkinter.Button(self.bfm,text="离开",command=self.top.quit,width=6, height=1)
        self.bqut.pack(padx=20,side=tkinter.LEFT)
        self.bfm.pack(pady=20,side=tkinter.BOTTOM)
####################################################################
#
####################################################################
    def start(self):
        if (self.version.get() != "") and (self.model.get() != "") and (self.projectPath.get() != "") and (self.buildPath.get() != "" and (self.keyWord.get() != "")):
            k = ReleaseKernel(self.keyWord.get(),self.version.get(),self.model.get(),self.projectPath.get(),self.buildPath.get(),self.merge.get(),self.select.get())
            if(k.getCondition() == 1):
                k.start()
            else:
                print("ERROR: kernel init error")

####################################################################
#
####################################################################
    def inputProjectPath(self):
        path = tkinter.filedialog.askdirectory()
        #change "/" to "\\"
        #path = "\\".join(path.split(r"/"))
        self.projectPath.set(path)

####################################################################
#
####################################################################
    def inputBuilderPath(self):
        path = tkinter.filedialog.askdirectory()
        #change "/" to "\\"
        #path = "\\".join(path.split(r"/"))
        self.buildPath.set(path)  

####################################################################
#
####################################################################
def main():
    g = ReleaseGUI()
    tkinter.mainloop()

####################################################################
#
####################################################################
if __name__=="__main__":
    main()

