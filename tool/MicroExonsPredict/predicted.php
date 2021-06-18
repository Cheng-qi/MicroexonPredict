<?php
$d = $_POST["POSITIONS"];
file_put_contents("../input.txt",$d);
//$command = "python ../run.py -p $d";
$command = "python3 ../run.py 2>&1";
$s = system($command);


if(substr($s, -8)=="FINISHED"){

    $str1 = str_replace("\n","<br>",file_get_contents("../result.txt"));
    echo  "<br>".str_replace("\t","&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;",$str1);
}else{
    echo "Wrong format!!";
}
?>

<a href="http://beian.miit.gov.cn/" target="_blank"style="bottom:10px;right:20px;z-index: 10;position: absolute;font-size: 11px">晋ICP备20001893号-1</a>