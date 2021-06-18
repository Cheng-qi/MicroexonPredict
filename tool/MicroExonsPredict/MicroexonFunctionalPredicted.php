<?php
?>
<html>
<head>

</head>
<body>

<div class='container entry-content'>

<p style="font-size:30px;font-weight: bold">
    Prediction of Functional Microexons with Transferring Learning
</p>
    <p><FORM enctype='multipart/form-data' ACTION="predicted.php" METHOD="post">
        <table border="0">

            <tr>
                <td align=left> <Strong>Input your Microexon Position(s) based on 0: </Strong><INPUT TYPE="button" NAME="clear sequence" VALUE="Clear" onClick="this.form.POSITIONS.value=''">
                </td>
                <td><font color=red><b>Every length is less than 30bt, and integer multiple of 3 </b></font></td>
            </tr>
            <tr>
                <td colspan=2>
                <textarea id="myinput" name="POSITIONS" rows=8 cols=112 required>
chr3:78696778:78696805
chr3:78742497:78742506
</textarea>
                </td>
            </tr>
        </table>
        <INPUT TYPE="SUBMIT" VALUE="Submit">
        <INPUT TYPE="RESET" VALUE="Reset">
    </FORM></p>

</div>

<a href="http://beian.miit.gov.cn/" target="_blank"style="bottom:10px;right:20px;z-index: 10;position: absolute;font-size: 11px">晋ICP备20001893号-1</a>
</body>


</html>
