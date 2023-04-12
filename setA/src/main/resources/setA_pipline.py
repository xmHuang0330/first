# -*- coding: utf-8 -*-
#setA pipline
import os
import sys
import time
from multiprocessing import Pool, Manager
from subprocess import Popen, PIPE, STDOUT

import xlrd


class Error(Exception):
    pass


class CommandError(Error):

    def __init__(self, code, message):
        self.code = code
        self.message = message


class executeCmd(object):
    def __init__(self, cmd_name="", cmd=""):
        super(executeCmd, self).__init__()
        self.cmd_name = cmd_name
        self.cmd = cmd

    # without in and out stream,set out to log.
    # 没有输入，输出到log文件
    def without_input(self,cmd_name, cmd, shell=False):
        self.cmd_name = cmd_name
        self.cmd = cmd
        line = b''
        try:
            proc = Popen(self.cmd,stdout=PIPE,stderr=STDOUT)
            count = 0
            while proc.poll() is None:
                # print(proc)
                count += 1
                print(f'\r{self.cmd_name}:\t等待。。。({count}',end='')
                time.sleep(1)
            if proc.returncode != 0:
                raise CommandError(proc.returncode,f'\r{self.cmd_name}:\t运行结束 ，但是返回值是{proc.returncode},请查看log文件信息，脚本结束。')
        except CommandError as e:
            exit(e.message)
        print(f'\r{self.cmd_name}:\t完成。。。               ')
        return proc.stdout.read().decode('utf-8').strip()

    def without_input_output(self,cmd_name,cmd,shell=False):
        self.cmd_name = cmd_name
        self.cmd = cmd
        try:
            proc = Popen(self.cmd,stdout=open(f'{workdir}/log/{cmd_name}.log','w'),stderr=STDOUT)
            count = 0
            while proc.poll() is None:
                # print(proc)
                count += 1
                print(f'\r{self.cmd_name}:\t等待。。。({count}',end='')
                time.sleep(1)
            print(proc.returncode)
            if proc.returncode != 0:
                raise CommandError(proc.returncode,f'\r{self.cmd_name}:\t子程序运行结束 ，但是返回值是{proc.returncode},请查看log文件信息，脚本结束。')
        except CommandError as e:
            exit(e.message)
        print(f'\r{self.cmd_name}:\t完成。。。               ')


    #有输入文件，也有输出文件，用于str8rzr
    def with_input_output(self,inputFile,outputFile,errFile):
        try:
            proc = Popen(self.cmd,stdin=open(inputFile,'r'),stdout=open(outputFile,'w'),stderr=open(errFile,'w'))
            while proc.poll() is None:
                print(f'\r{self.cmd_name}:\t正在处理{inputFile}...',end='')
                time.sleep(1)
            if proc.returncode != 0:
                # os.unlink()
                raise CommandError(proc.returncode,f'\r{self.cmd_name}:\t子程序运行结束 ，但是返回值是{proc.returncode},请查看log文件信息，脚本结束。')
        except CommandError as e:
            exit(e.message)
        except Exception as e:
            raise e

def readSampleXlsx(file,output):
    sheet = xlrd.open_workbook(file).sheet_by_index(0)
    samples = list()
    with open(output,'w',encoding='utf-8') as outFile:
        for i in range(1,sheet.nrows):
            line = list()
            for j in [0,3,4]:
                cell_value = sheet.cell(i,j).value
                if isinstance(cell_value,float):
                    cell_value = str(int(cell_value))
                line.append(cell_value)
                if j == 0:
                    samples.append(cell_value)
            outFile.write("\t".join(line)+'\n')
    return samples
def make_dirs():
    try:
        for directory in ['log','split','STR_SNP_OUTPUT','report']:
            if not os.path.exists(f'{workdir}/{directory}/'):
                os.makedirs(f'{workdir}/{directory}/')
    except PermissionError as e:
        exit(f'{directory}文件夹创建失败，无权限')

def getBestMatch(shared_strs):
    longest_key = ""
    most_match_time = 0
    for k,v in sorted(shared_strs.items(),key=operator.itemgetter(1),reverse=True):
        print(k,v)
        if v > most_match_time:
            most_match_time = v
            longest_key = k
        elif v == most_match_time:
            if len(k) > len(longest_key):
                most_match_time = v
                longest_key = k
    return longest_key

def getPrefixAndSuffix(directory):
    file_names = list()
    rawdata_suffix = ".gz"
    if len([i for i in os.listdir(directory) if i.endswith(rawdata_suffix) ]) < 1:
        rawdata_suffix = ".txt"
    for i in os.listdir(directory):
        if os.path.isfile(os.path.join(directory,i)) & i.endswith(rawdata_suffix):
            file_names.append(os.path.split(i)[-1])
    # prefix
    shared_strs = dict()
    sample = file_names[0]
    for item in file_names:
        for x in range(1,len(sample)):
            sub_str = sample[0:x]
            if item.startswith(sub_str):
                if sub_str in shared_strs:
                    shared_strs[sub_str]+=1
                else:
                    shared_strs[sub_str] = 1
    prefix = getBestMatch(shared_strs)
    for i in file_names:
        if not i.startswith(prefix):
            prefix = ""

    shared_strs = dict()
    sample = file_names[0]
    for item in file_names[1:]:
        for x in range(1,len(sample)):
            sub_str = sample[x:len(sample)]
            if item.endswith(sub_str):
                if sub_str in shared_strs:
                    shared_strs[sub_str]+=1
                else:
                    shared_strs[sub_str] = 1
    # print(shared_strs)
    suffix = getBestMatch(shared_strs)
    for i in file_names:
        if not i.endswith(suffix):
            suffix = ""
    return prefix,suffix
#str多进程
def multiProc(cmd_name,cmd,queue):
    proc = executeCmd()
    while not queue.empty():
        sample = queue.get(True)
        for i in ['SNP','STR']:
            proc.cmd_name = f'{cmd_name}_{sample}_{i}'
            proc.cmd = cmd[i]
            proc.with_input_output(f'{workdir}/split/{prefix}{sample}{suffix}',f'{workdir}/STR_SNP_OUTPUT/{sample}_{i}.out',f'{workdir}/log/{sample}_{i}.err')

error = """
参数有误：
需要如下参数
process:流程起始
    0) 拆分至结束
    1) STR/SNP分型至结束
    2) 统计和报告生成
workdir:总的工作目录，每一步的输出目录是固定在该目录下的（workdir/[split|STR_SNP_OUTPUT|report]）
infile:	未拆分的fq.gz文件
index:	拆分fq使用的index_recode文件，也作为分型、统计报告的样本列表，第二列应当为样本编号。
nth:	str分型进程数
nproc:	str分型每个进程使用的核心数（nth*nproc不应大于cpu核心数）

注意：假设infile为V300028791_L01_read.fq.gz,样本为1。 
    str分型会寻找workdir/split/V300028791_L01_read.fq_1.txt作为输入文件，输出则为1_STR.out
    没有infile（已拆分），可以使用null
    """
def main():
    global workdir,programDir,sampleXlsx,infile,infile_name,workdir
    try:
        python, progress, sampleXlsx, workdir, infile, index, nth, nproc = sys.argv
        # python, progress, workdir, infile, index, nth, nproc = sys.argv
    except ValueError as e:
        exit(error)

    programDir = os.path.dirname(python)
    make_dirs()

    cmd = executeCmd()

    ###
    #最大打开的文件数
    ###
    import platform
    if platform.system() != 'Windows':
        import resource
        resource.setrlimit(resource.RLIMIT_NOFILE,(3000,3000))

    ###
    #0.拆fq
    ###

    get_batch = cmd.without_input("获取样本编号",['awk','BEGIN{ORS=","}{print $2}',index])
    samples = get_batch.strip().strip(',').split(',')


    # samples = readSampleXlsx(sampleXlsx,workdir+'/samplelist.txt')
    print("成功读取样本信息单")
    def split_fq(fq_file,index_file):
        python2 = cmd.without_input("获取python2路径",['which','python2'])
        print(time.strftime('%Y-%m-%d %H:%M:%S',time.localtime(time.time())))

        split_fq = cmd.without_input_output("拆分fq文件",[python2, programDir+'/gSplitFastQIndex.py','-o', workdir+'/split', '-i',
                                            index_file, '-l', '10', '--bases_after_index',
                                            'AACTCCTTGGCTCACA', '-v', '--summary', fq_file])

    ###
    #1.分析STR\SNP
    ###
    def analysis_str_snp(nth,nproc):
        str_command = {
            'SNP':[programDir+'/str8rzr','-p',nproc,'-v','-c',programDir+'/snp_config'],
            'STR':[programDir+'/str8rzr','-p',nproc,'-v','-c',programDir+'/str_config']
        }
        nth = int(nth)
        q = Manager().Queue()
        p = Pool(nth)
        for sample in samples:
            q.put(sample)
        # multiProc('分析',str_command,q)

        for i in range(nth):
            p.apply_async(multiProc,('分析',str_command,q,))
        p.close()
        p.join()
    ###
    #2.合并统计
    ###
    def stat():
        for i in ['STR','SNP']:
            totalMatch = {'MarkerName/Sample':samples}
            for sample in samples:
                print(f'\r合并统计{i}：\t{sample}...',end='')
                with open(f'{workdir}/STR_SNP_OUTPUT/{sample}_{i}.out','r') as file:
                    flag = True
                    for line in file.readlines():
                        if flag:
                            if line.startswith('MarkerName'):
                                flag = False
                                continue
                            continue
                        MarkerName, MissingRightanchor_Counts, TotalMatches_Count, Ratio, AvgLeftPos, AvgRightPos = line.split('\t')
                        if MarkerName not in totalMatch:
                            totalMatch[MarkerName] = list()
                        totalMatch[MarkerName].append(TotalMatches_Count)
                    if flag:
                        totalMatch['MarkerName/Sample'].remove(sample)

            with open(f'{workdir}/{i}_TotalMatches.txt','w') as file:
                for k,v in totalMatch.items():
                    file.write(k+'\t'+'\t'.join(v)+'\n')
            print(f'\r合并统计{i}：\t完成...                   ')

        with open(f'{workdir}/samples.list','w') as file:
            for sample in samples:
                file.write(f'{workdir}/STR_SNP_OUTPUT/{sample}\n')
        report = cmd.without_input_output('生成报告',['java','-jar',programDir+'/fy_excel-1.0.jar',
                                                            '-i',f'{workdir}/samples.list',
                                                            '-o',workdir+'/report/',
                                                            '-rawdataPath',f'{workdir}/split/',
                                                            '-worker',f'{nth*nproc}'])

    #progress,workdir,
    #步骤，工作目录
    if progress == '0':
        #fq.gz文件，index_recode文件
        split_fq(infile,index)
        getPrefixAndSuffix(f'{output}/split/')
        analysis_str_snp(nth,nproc)
        stat()
    elif progress == '1':
        analysis_str_snp(nth,nproc)
        stat()
    elif progress == '2':
        #infile 是index_barcode文件,需要文件第二列的样本编号，或者指定另一个文件，需要第二列是样本编号
        stat()
    else:
        exit(error)


    print(time.strftime('%Y-%m-%d %H:%M:%S',time.localtime(time.time())))
if __name__ == '__main__':
    try:
        main()
    except KeyboardInterrupt as e:
        exit('用户取消')