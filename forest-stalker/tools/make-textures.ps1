# Generates all PNG assets for the Forest Stalker mod.
# Usage: powershell -ExecutionPolicy Bypass -File tools/make-textures.ps1
Add-Type -AssemblyName System.Drawing

$root = Split-Path -Parent $PSScriptRoot
$texDir = Join-Path $root "src\main\resources\assets\foreststalker\textures"
New-Item -ItemType Directory -Force -Path (Join-Path $texDir "items") | Out-Null
New-Item -ItemType Directory -Force -Path (Join-Path $texDir "entity") | Out-Null

function New-Bitmap([int]$w, [int]$h) {
    return New-Object System.Drawing.Bitmap($w, $h, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
}

function Set-Px($bmp, [int]$x, [int]$y, [string]$hex) {
    if ($x -lt 0 -or $y -lt 0 -or $x -ge $bmp.Width -or $y -ge $bmp.Height) { return }
    $c = [System.Drawing.ColorTranslator]::FromHtml($hex)
    $bmp.SetPixel($x, $y, $c)
}

function Save-Px($bmp, [string]$path) {
    $bmp.Save($path, [System.Drawing.Imaging.ImageFormat]::Png)
    $bmp.Dispose()
    Write-Host "wrote $path"
}

$rand = New-Object System.Random 1337

function Get-Shade([int]$base, [int]$delta) {
    $v = $base + $rand.Next(-$delta, $delta + 1)
    if ($v -lt 0) { $v = 0 }
    if ($v -gt 255) { $v = 255 }
    return [int]$v
}

function Fill-Region($bmp, [int]$x0, [int]$y0, [int]$w, [int]$h, [int]$r, [int]$g, [int]$b, [int]$delta) {
    for ($y = $y0; $y -lt $y0 + $h; $y++) {
        for ($x = $x0; $x -lt $x0 + $w; $x++) {
            $col = [System.Drawing.Color]::FromArgb(255, (Get-Shade $r $delta), (Get-Shade $g $delta), (Get-Shade $b $delta))
            if ($x -ge 0 -and $y -ge 0 -and $x -lt $bmp.Width -and $y -lt $bmp.Height) { $bmp.SetPixel($x, $y, $col) }
        }
    }
}

# ---------------------------------------------------------------- tree blade 16x16
$sword = New-Bitmap 16 16
$art = @(
    "................",
    ".............OO.",
    "............OBB.",
    "...........OBBO.",
    "..........OBBO..",
    ".........OBBO...",
    "........OBBO....",
    ".......OBBO.....",
    "......OBBO......",
    ".....OBBO.......",
    "....OBBO........",
    "...GGBO.........",
    "..GGHGO.........",
    "..HGGO..........",
    ".HHO............",
    "HO.............."
)
$palette = @{
    'O' = '#1a2110'   # dark outline
    'B' = '#8a5a2b'   # wooden blade
    'G' = '#3f7a3a'   # mossy guard
    'H' = '#3b2a17'   # handle
}
for ($y = 0; $y -lt 16; $y++) {
    for ($x = 0; $x -lt 16; $x++) {
        $ch = [string]$art[$y][$x]
        if ($ch -ne '.' -and $palette.ContainsKey($ch)) { Set-Px $sword $x $y $palette[$ch] }
    }
}
# a lighter wooden edge along the blade
foreach ($p in @(@(13,1), @(12,2), @(11,3), @(10,4), @(9,5), @(8,6), @(7,7), @(6,8), @(5,9))) {
    Set-Px $sword $p[0] $p[1] '#c08a4a'
}
# a couple of wood grain flecks
Set-Px $sword 9 5 '#6e4520'
Set-Px $sword 7 7 '#6e4520'
Set-Px $sword 5 9 '#6e4520'
# glowing moss in the guard
Set-Px $sword 3 12 '#8fe06a'
Save-Px $sword (Join-Path $texDir "items\stalker_blade.png")

# ---------------------------------------------------------------- egg 16x16
$egg = New-Bitmap 16 16
$eggArt = @(
    "................",
    "................",
    "......OOOO......",
    ".....OLLLLO.....",
    "....OLLLLLLO....",
    "...OLLSLLLLLO...",
    "...OLLLLLSLLO...",
    "...OLLSLLLLLO...",
    "...OLLLLLLSLO...",
    "...OLLSLLLLLO...",
    "....OLLLLLLO....",
    "....OLLSLLLO....",
    ".....OLLLLO.....",
    "......OOOO......",
    "................",
    "................"
)
$eggPalette = @{
    'O' = '#241a0e'
    'L' = '#6b4a24'
    'S' = '#9ec46a'
}
for ($y = 0; $y -lt 16; $y++) {
    for ($x = 0; $x -lt 16; $x++) {
        $ch = [string]$eggArt[$y][$x]
        if ($ch -ne '.' -and $eggPalette.ContainsKey($ch)) { Set-Px $egg $x $y $eggPalette[$ch] }
    }
}
Save-Px $egg (Join-Path $texDir "items\stalker_egg.png")

# ---------------------------------------------------------------- entity atlas 128x128
# Regions (must match the UV offsets in ModelForestStalker):
#   bark  : everything except the regions below
#   leaves: x 80..127, y  0..47
#   wood  : x 80..127, y 48..95
#   eyes  : x 96..111, y 96..111
$skin = New-Bitmap 128 128

# whole sheet: mossy bark
Fill-Region $skin 0 0 128 128 72 84 58 12

# bark texture: dark horizontal cracks and lighter moss
for ($i = 0; $i -lt 130; $i++) {
    $x = $rand.Next(0, 96); $y = $rand.Next(0, 128)
    $len = $rand.Next(2, 7)
    for ($d = 0; $d -lt $len; $d++) { Set-Px $skin ($x + $d) $y '#3d4a30' }
}
for ($i = 0; $i -lt 90; $i++) {
    $x = $rand.Next(0, 96); $y = $rand.Next(0, 128)
    Set-Px $skin $x $y '#8aa75a'
}
for ($i = 0; $i -lt 60; $i++) {
    $x = $rand.Next(0, 96); $y = $rand.Next(0, 128)
    Set-Px $skin $x $y '#5a6b44'
}

# leaves region
Fill-Region $skin 80 0 48 48 42 88 42 20
for ($i = 0; $i -lt 220; $i++) {
    $x = $rand.Next(80, 128); $y = $rand.Next(0, 48)
    $shade = @('#2f6b31', '#69a94a', '#4b8a38', '#8cc45e')[$rand.Next(0, 4)]
    Set-Px $skin $x $y $shade
}

# wood region: vertical grain
Fill-Region $skin 80 48 48 48 104 72 38 10
for ($x = 80; $x -lt 128; $x += 3) {
    for ($y = 48; $y -lt 96; $y++) {
        if ($rand.Next(0, 100) -lt 70) { Set-Px $skin $x $y '#6d4520' }
    }
}
for ($i = 0; $i -lt 40; $i++) {
    $x = $rand.Next(80, 128); $y = $rand.Next(48, 96)
    Set-Px $skin $x $y '#c08a4a'
}

# glowing eyes region
Fill-Region $skin 96 96 16 16 196 240 96 18
for ($x = 99; $x -lt 109; $x++) { Set-Px $skin $x 103 '#f4ffc8' }
for ($y = 100; $y -lt 107; $y++) { Set-Px $skin 103 $y '#f4ffc8' }
Set-Px $skin 103 103 '#ffffff'

# face: brow shadow and jagged mouth on the head front face (x 8..18, y 64..72)
for ($x = 8; $x -lt 18; $x++) { Set-Px $skin $x 65 '#2c3524' }
foreach ($p in @(@(9,69), @(10,69), @(11,70), @(12,69), @(13,69), @(14,70), @(15,69), @(16,69))) {
    Set-Px $skin $p[0] $p[1] '#1c2216'
}

Save-Px $skin (Join-Path $texDir "entity\stalker.png")
