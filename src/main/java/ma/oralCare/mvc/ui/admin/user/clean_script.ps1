$lines = Get-Content 'c:\Users\zinebboulahbach\IdeaProjects\OralCare\src\main\java\ma\oralCare\mvc\ui\admin\user\UserListView.java'
$cleanLines = @()
foreach($line in $lines) {
    if($line.Trim() -ne '') {
        $cleanLines += $line
    }
}
$cleanLines | Set-Content 'c:\Users\zinebboulahbach\IdeaProjects\OralCare\src\main\java\ma\oralCare\mvc\ui\admin\user\UserListView_clean.java'
