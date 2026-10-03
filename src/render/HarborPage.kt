package com.rafambn.profilebanner.render

import com.rafambn.profilebanner.PortfolioStats

fun renderHarborPage(stats: PortfolioStats): String = """
<!doctype html>
<html lang="en">
<head>
 <meta charset="utf-8">
 <meta name="viewport" content="width=device-width, initial-scale=1">
 <meta name="description" content="Rafael Mendonça. Solutions architect. Making complex things simple. Explore six open-source projects in a harbor built for discovery.">
 <meta name="theme-color" content="#087fa5">
 <title>Rafael Mendonça · Open source</title>
 <style>
  *{box-sizing:border-box}html{background:#fcf5e2;color:#062c43;scrollbar-color:#087fa5 #fcf5e2}
  body{margin:0}::selection{background:#f1c94b;color:#062c43}
  main{max-width:1344px;margin:0 auto;padding:20px 24px 32px}
  .harbor-scene{display:block;width:100%;height:auto}
  .harbor-mobile{display:none}
  @media(max-width:900px){main{padding:0;max-width:560px}.harbor-desktop{display:none}.harbor-mobile{display:block}}
 </style>
</head>
<body><main aria-label="Rafael Mendonça's portfolio">
 <div class="harbor-desktop">${renderHarborSvg(stats, HarborLayout.DESKTOP).substringAfter("?>")}</div>
 <div class="harbor-mobile">${renderHarborSvg(stats, HarborLayout.MOBILE).substringAfter("?>")}</div>
</main></body>
</html>
""".trimIndent()

fun renderImagePreview(): String = """
<!doctype html>
<html lang="en"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>Harbor image preview</title>
<style>body{margin:0;background:#fcf5e2}main{max-width:840px;margin:auto;font-size:0}img{display:block;width:100%;height:auto}a{display:block}a:focus-visible{outline:3px solid #062c43;outline-offset:-3px}</style></head>
<body><main aria-label="Standalone harbor image strips">
${renderReadmeImages("/preview")}
</main></body></html>
""".trimIndent()

fun renderReadmeImages(base: String): String = buildString {
    append("""<div><a href="https://github.com/rafambn"><picture><source media="(max-width: 760px)" srcset="$base/header.svg?layout=mobile"><img src="$base/header.svg" width="100%" align="top" alt="Rafael Mendonça. Solutions architect. Making complex things simple. Profile views: Today, Week, Month and Total."></picture></a></div>
""")
    for (repository in com.rafambn.profilebanner.pinnedRepos) {
        append("""<div><a href="https://github.com/rafambn/$repository"><picture><source media="(max-width: 760px)" srcset="$base/projects/$repository.svg?layout=mobile"><img src="$base/projects/$repository.svg" width="100%" align="top" alt="$repository repository. GitHub stars and recorded views. Open $repository on GitHub."></picture></a></div>
""")
    }
    append("""<div><a href="https://github.com/rafambn"><picture><source media="(max-width: 760px)" srcset="$base/footer.svg?layout=mobile"><img src="$base/footer.svg" width="100%" align="top" alt="Visit Rafael Mendonça on GitHub."></picture></a></div>""")
}
