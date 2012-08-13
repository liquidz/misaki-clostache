{
 ;; directory setting
 :public-dir   "public/"
 :template-dir "template/"
 :compiler "clostache"
 :post-dir "posts/"
 :layout-dir "layouts/"
 :post-filename-regexp #"(\d{4})-(\d{1,2})-(\d{1,2})[-_](.+)$"
 :post-filename-format "{{year}}-{{month}}/{{filename}}"

 :compile-with-post ["index.html"]

 :site {:msg "config hello"}
 }

