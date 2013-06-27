{
 ;; directory setting
 :public-dir   "public/"
 :template-dir "template/"
 :compiler "clostache"
 :post-dir "posts/"
 :layout-dir "layouts/"

 :url-base "/"

 :site {:atom-base  "http://localhost:8080"
        :site-title "clostache plugin sample"
        :twitter    "uochan"}

 :post-filename-regexp #"(\d{4})-(\d{1,2})-(\d{1,2})[-_](.+)$"
 :post-filename-format "{{year}}-{{month}}/{{filename}}"

 :posts-per-page 2


 :compile-with-post ["index.html"]
 }

