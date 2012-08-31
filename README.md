![misaki logo](https://github.com/liquidz/misaki/raw/master/samples/blog/public/img/logo.png)

misaki-clostache is one of [misaki](https://github.com/liquidz/misaki)'s compiler plugin using [Clostache](https://github.com/fhd/clostache).

This compiler allows you to use misaki with HTML templates instead of S-exp templates.

## Example template

```html
; @layout default
; @title  sample title

<h1>{{title}}</h1>

<h2>posts</h2>
<ul>
    {{#posts}}
    <li><a href="{{url}}">{{date}} - {{title}}</a></li>
    {{/posts}}
</ul>
```

## Usage

### Run sample

```bash
$ git clone git://github.com/liquidz/misaki-clostache.git
$ cd misaki-clostache
$ lein run sample
```

## License

Copyright (C) 2012 Masashi Iizuka([@uochan](http://twitter.com/uochan/))

Distributed under the Eclipse Public License, the same as Clojure.
