t.custom"),o="Shared",s;(function(n){n[n.EVENT=0]="EVENT";n[n.MASTER_PAGE_IMPRESSION=1]="MASTER_PAGE_IMPRESSION"})(s||(s={}));var w="MUID",b=null,et="CIQueueError",i,gi=864e5,ot,k,e,st=[],ht=[],f,v,wt,bt=0,nr=typeof _G=="object"&&_G.RevIpCC&&_G.RevIpCC.toUpperCase()||"",y=nr=="CN"?"https://cn.bing.com":"https://www.bing.com";(function(n){function p(){return o&&i.isInstrumentationEnabled}function w(n){var u,i;if(n===void 0&&(n=!1),p()){u=st(n);try{i=n?c:f;e[i]=u;e[i+"_logUploadIntervalStartDate"]=r;e[i+"_uploadedLogSizeInInterval"]=t}catch(s){if(s.name.toLowerCase().indexOf("quota")>=0)o=!1;else throw s;}}}function l(){p()&&(h&&sb_ct(h),h=sb_st(w,i.queueDumpInterval))}function g(n,t){var i=JSON.stringify(n),r=i.length+3;return n.size=r,t?i.replace('"size":0','"size":'+r):i}function nt(n){return b===null&&typeof _CachedFlights!="undefined"&&_CachedFlights.sort&&(b=_CachedFlights.sort().join(",")),{log:n,lastSendErrorTimeStamp:0,inProgress:!1,size:0,flights:b}}function tt(){var t,r,i;if(e){if(ct(),t=e[f],n.queue=[],typeof t=="string"||t&&t.length!==0)try{if(n.queue=JSON.parse(t),n.queue.some(function(n){return!n.log}))u("PrimaryQueueRestoreInvalidItems",n.queue.length,_G.IG),n.queue=[];else if(r=n.queue.length,r>0){for(i=0;i<r;i++)n.queue[i].inProgress=!1;lt(!0)}}catch(s){u("PrimaryQueueRestoreFailed",0,_G.IG)}e[f]="[]";o=!0}}function it(t){var a=[],v=[],f=t?n.retryList[0]:n.queue,h,o,y,p,l,r,w;if(d){if(h=e[c],typeof h=="string"&&h.length!==0)try{o=JSON.parse(h);o.some(function(n){return!n.log})?u("SharedQueueRestoreInvalidItems",o.length,_G.IG):f?Array.prototype.push.apply(f,o):f=o}catch(b){u("SharedQueueRestoreFailed",0,_G.IG)}e[c]="[]"}if(f)for(y=0,p=f.length,l=0;l<p;l++)if(r=f[l],t||!r.lastSendErrorTimeStamp)if(ClientInstConfig.enableBatSizeError&&r.size>i.maxBatchSize&&u("ElemBatchSizeLimitReached",r.size,_G.IG),y+=r.size,y<=i.maxBatchSize)r.inProgress=!0,w=r.log,w.type==s.MASTER_PAGE_IMPRESSION?v.push(r):a.push(r);else break;return{events:a,masterPageImpressions:v,length:a.length+v.length,isRetryBatch:t}}function rt(t){return n.queue=n.queue.filter(function(n){return!n.inProgress&&(!t||!!n.lastSendErrorTimeStamp)}),l(),n.queue.length>0}function ut(n){return n.log.type===s.EVENT&&n.log.data&&n.log.data.eventType===et}function ft(t,i){for(var f=[],s=i?n.retryList[0]:n.queue,e=0,r,o,h;e<s.length;)r=s[e],r.inProgress?t?(s.splice(e,1),ut(r)||(r.lastSendErrorTimeStamp=(new Date).getTime(),f.push(r))):(r.inProgress=!1,e++):e++;o=f.length;o==1?u("InvalidLogMessage",1,f[0].log.impressionGuid):o>0&&(h=o/2,n.retryList.push(f.slice(0,h)),n.retryList.push(f.slice(h)));l()}function ot(){var t,i,r;if(n.retryList.length>0){for(t=n.retryList[0],i=0;i<t.length;)r=t[i],r.inProgress?t.splice(i,1):i++;t.length==0&&n.retryList.shift()}}function st(t){var c=JSON.stringify(n.queue),f=c.length-i.maxStorageUse,e,o,r,s,h;if(v=0-f,e=n.queue.length,f>0)for(o=0,r=0;r<e;r++)if(s=n.queue[r].size,o+=s+1,o>=f){n.queue.splice(0,r+1);u("QueueOverflow",r+1,_G.IG,!0);break}return h=JSON.stringify(n.queue),t&&n.queue.splice(0,e),h}function ht(t){var r=nt(t);g(r,!1);ClientInstConfig.enableBatSizeError&&r.size>i.maxBatchSize&&u("EnvelopeBatchSizeLimitReached:"+t.type,r.size,_G.IG,undefined);r.size>i.maxBatchSize?u("LogObjectTooLarge",1,_G.IG,!0):at(r.size)&&(i.optimizeFlush?v>=r.size?n.queue.push(r):u("QueueFullDropLog",1,_G.IG,!0):n.queue.push(r));l()}function ct(){var u=f+"_logUploadIntervalStartDate",o=f+"_uploadedLogSizeInInterval",n;r=e[u];t=e[o];n=sb_gt();r==undefined||t==undefined?a(n):k(r,n)>=i.logUploadCapIntervalInDays&&a(n)}function a(n){r=n;t=0}function k(n,t){var i=t-n;return i/gi}function at(n){var f,e;return i.isInstrumentationEnabled?LogUploadCapFeatureEnabled?(f=sb_gt(),k(r,f)>=i.logUploadCapIntervalInDays&&a(f),t>=i.logUploadCapSizeInChar)?!1:(e=t+n,e>=i.logUploadCapSizeInChar)?(u("LogUploadSizeLimitReached",1,_G.IG,!0),t=i.logUploadCapSizeInChar,!1):(t=e,!0):!0:!1}n.queue=[];var r,t,o=!1,h=null,v=5e5;n.retryList=[];var y=_w.location.pathname,f=InstLogQueueKeyFetcher.Get(y),c=InstLogQueueKeyFetcher.GetSharedLocation(),d=InstLogQueueKeyFetcher.CanUploadSharedMessages(y);n.dumpToStorage=w;n.initialize=tt;n.getBatch=it;n.clearSentItems=rt;n.markFailedItems=ft;n.recordRetryAttempt=ot;n.append=ht})(r||(r={}));var dt=0,gt=0,ni=0,ti=0,ii=0,d="";t.SetFeatures=ri;c={mainQueue:{getInterval:function(){return i.flushInterval}},retryQueue:{getInterval:function(){return i.retryInterval}}};t.SetOnInstrumentDone=hi;t.LogEvent=vt;t.LogMasterPageImpression=ci;t.LogMasterPageImpressionWSB=li;ai=function(n,t,r,u){for(var e=[],f=4;f<arguments.length;f++)e[f-4]=arguments[f];i.isInstrumentationEnabled&&(wt||(it("Init","CI","Base"),wt=!0),it(n,t,r,u,e))};t.Log=ai;vi=function(n,i,r,u,f,e,o){t.Log2(n,i!==null&&i!==void 0?i:r,null,null,u,f,o)};t.LogInstrumented=vi;yi=function(n,t,i,r,u,f,e){var o=Object.keys(e).reduce(function(n,t){return __spreadArray(__spreadArray([],n,!0),[t,e[t]],!1)},[]);i&&o.push("service",i);r&&o.push("scenario",r);u&&o.push("appNS",u);f&&o.push("kValue",f);it(n,null,t,!1,o)};t.Log2=yi;it=function(n,t,r,u,f){var a,e,c,l,o,h,v;if(i.isInstrumentationEnabled){if(a=_G.IG,e={},f&&f.length>0&&f.length%2==0)for(c=0;c<f.length;c+=2)(l=f[c],l)&&(o=l.toLowerCase(),h=f[c+1],o==="impressionguid"?a=h:o==="service"?e.Service=h:o==="scenario"?e.Scenario=h:o==="appns"?e.AppNS=h:o==="k"||o==="kvalue"?e.K=h:o==="pos"?e.Pos=h:e[l]=h);v=(new Date).getTime();e.T="CI.".concat(n);e.TS=v;e.RTS=v-k;e.SEQ=ot++;e.Name=r!==null&&r!==void 0?r:"";e.FID=typeof t!="number"?t!==null&&t!==void 0?t:"":"";e.hasOwnProperty("K")||typeof t!="number"||(e.K=t);p(e);g({type:s.EVENT,impressionGuid:a,previousImpressionGuid:null,timestamp:k,data:{eventType:"ClientInst",eventData:e}})}};t.ForceFlush=bi;ir();t.FlushMainQueueDontForce=rt;t.SaveLogsToSharedStorage=ki;t.SaveLogsToLocalStorage=pt;t.ResetState=di;typeof h!="undefined"&&h.bind&&(h.bind("onP1",rt,!0),h.bind("ajax.postload",rt,!0));typeof ft!="undefined"&&ft.bind&&ft.bind(_w,"beforeunload",pt,!1);_w.Log={Log:t.Log};_w.Log2={LogEvent:vt,LogMasterPageImpression:ci,LogMasterPageImpressionWSB:li,ForceFlush:bi,FlushMainQueueDontForce:rt,SaveLogsToSharedStorage:ki,ResetState:di,SaveLogsToLocalStorage:pt,SetOnInstrumentDone:hi,SetFeatures:ri};_w.Shared2=_w.Shared2||{};_w.Shared2.Log={Log:t.Log,LogInstrumented:t.LogInstrumented};_w.sj_log2=t.Log2}),function(n){function i(n){for(var r=[],i=1;i<arguments.length;i++)r[i-1]=arguments[i];return t.apply(null,[null,n].concat(r))}function t(n,t){for(var i=[],r=2;r<arguments.length;r++)i[r-2]=arguments[r];return function(){for(var f,r=[],u=0;u<arguments.length;u++)r[u]=arguments[u];if(r&&r.length!==0)for(f in i)i.hasOwnProperty(f)&&r.push(i[f]);else r=i;return t.apply(n,r)}}function r(){for(var n,r,t=[],i=0;i<arguments.length;i++)t[i]=arguments[i];for(n=t[0],r=1;r<t.length;r++)if(n)n=n[t[r]];else return null;return n}n.deferFunction=i;n.deferMethod=t;n.getProperty=r;window.sj_df=i;window.sj_dm=t;window.sj_gp=r}(CoreUtilities||(CoreUtilities={})),function(n){function v(n,i,r,o,s){(new Image).src=_G.lsUrl+'&Type=Event.ClientInst&DATA=[{"T":"CI.Error","FID":"CI","Name":"JSError","Text":'+n+"}]";u(t.Error,f,e,n,i,r,o,s)}function y(n,i,r,o,s){u(t.Error,f,e,n,i,r,o,s)}function p(n,i,r){u(t.Warning,l,a,n,i,r)}function w(n){r=n}function u(n,u,f,e,o,h,c,l){var w="Error",d=n+e,v=u[d],g,a,p,y;(typeof v!="number"&&(v=0),r(h,e))||(g=e.substr(0,7)=="http://"||e.substr(0,8)=="https://"?"":e,!h&&o&&(h=o,o=undefined),a={Text:null,Stack:null,Meta:_w.location.href,Line:c,Char:l,Name:"JSError"},Object.defineProperty(a,"Text",{get:sj_df(b,a,g,o,n,h)}),Object.defineProperty(a,"Stack",{get:sj_df(k,a,o,n,h)}),v<f&&(p=JSON.stringify(a),y=!1,n===t.Error?(console.error(p),y=!0):n===t.Warning&&(console.warn(p),w="Warning",y=!0),Log2.LogEvent(s,a,w,null,null,null,null,null),u[d]=v+1,y&&i(a,"c",1)),sj_evt&&sj_evt.fire("ErrorInstrumentation",null,a,n,h))}function o(n,r,u){var f=n.errorLines,e;return f||(e=r===t.Error&&u?u.stack||u.message:null,f=(u?(e||u)+"":"").replace(h,"").replace(c,"\n").replace(location.href,"self").split("\n"),f.length>1&&f[0]===f[1]&&(f=f.slice(1)),i(n,"errorLines",f)),f}function b(n,t,r,u,f){var e=n.logTextCache,s,h;return e||(s=f?f.number:null,h=o(n,u,f),e=(t?"["+t+"] ":"")+(s?s+" ":"")+h[0],r=="Uncaught "+e&&(e=r),i(n,"logTextCache",e)),e}function k(n,t,r,u){var f=n.logStackCache,e;return f||(e=o(n,r,u),f=n.Text+(t&&t!=n.Text?"\n"+t:"")+(e.length>1?"\n"+e.slice(1).join("\n"):""),i(n,"logStackCache",f)),f}function i(n,t,i){Object.defineProperty(n,t,{value:i})}var t,r;(function(n){n[n.None=0]="None";n[n.Error=1]="Error";n[n.Warning=2]="Warning"})(t||(t={}));var s="ClientInst",h=/\r/g,c=/\n\n/g,f={},e=1,l={},a=1;n.LogFatalError=v;n.LogError=y;n.LogWarning=p;r=function(){return!1};n.RegisterErrorFilter=w}(LoggerModule||(LoggerModule={}));_w.SharedLogHelper={LogError:LoggerModule.LogError,LogWarning:LoggerModule.LogWarning,RegisterErrorFilter:LoggerModule.RegisterErrorFilter},function(n){function u(n,u,f){i||(n&&u&&(n.addEventListener("statechanged",r),sj_be(u,"visibilitychange",r,!1)),i=!0);t.indexOf(f)===-1&&t.push(f)}function f(){return t}function r(n){var u=!1,f,e,i,o,s,r;if(_w.clickFlushedTime&&sb_gt()-_w.clickFlushedTime<100&&(u=!0),n.synthetic!==!0)try{if(i=n.newState,typeof i!="undefined"?(e=i!==0,f=i===0?"hidden":"visible"):(e=!document.hidden,f=document.visibilityState),u||sj_evt.fire("visibility",e,i),f!=="visible"){for(o in t)s=t[o],s();_w.useSharedLocalStorage?Log2.SaveLogsToSharedStorage():u?Log2.FlushMainQueueDontForce():Log2.ForceFlush()}r=document.createEvent("event");r.initEvent("visibilitychange",!0,!0);r.synthetic=!0;document.dispatchEvent(r)}catch(h){SharedLogHelper.LogFatalError("SharedLogVisibilityFailure",null,h)}}var t=[],i=!1;n.Register=u;n.GetHandlers=f}(VisibilityChangeHelperModule||(VisibilityChangeHelperModule={}));_w.VisibilityChangeEventHelper={Register:VisibilityChangeHelperModule.Register,GetHandlers:VisibilityChangeHelperModule.GetHandlers};var sj_anim=function(n){var s=25,t=this,c,u,h,f,e,o,l,i,r;t.init=function(n,s,a,v,y){if(c=n,e=s,o=a,l=v,r=y,v==0){f=h;r&&r();return}i||(i=e);u||t.start()};t.start=function(){h=sb_gt();f=Math.abs(o-i)/l*s;u=setInterval(t.next,s)};t.stop=function(){clearInterval(u);u=0};t.next=function(){var u=sb_gt()-h,s=u>=f;i=e+(o-e)*u/f;s&&(t.stop(),i=o);n(c,i);s&&r&&r()};t.getInterval=function(){return s}},sj_fader=function(){return new sj_anim(function(n,t){sj_so(n,t)})},customEvents=require("event.custom");customEvents.bind("onPP",function(){customEvents.fire("onP1Lazy")},!0);
/*!DisableJavascriptProfiler*/
0;
/*!DisableJavascriptProfiler*/
(function(n){function e(n){return n?n.replace(u,"").replace(f,""):n}function h(t,i){var r,o;if(!t)return[];var h=[],c=!1,s=0;for(r=0;r<=t.length;++r)c?t[r]==n.endMarker&&(o={text:t.substring(s,r).replace(u,""),highlighted:!0},o.text.length>0&&h.push(o),c=!1,s=r+1):t[r]==n.startMarker&&(o={text:t.substring(s,r).replace(f,""),highlighted:!1},o.text.length>0&&h.push(o),c=!0,s=r+1);return s!=t.length&&(o={text:e(t.substring(s)),highlighted:c},h.push(o)),i&&h.forEach(function(n){return n.highlighted=!n.highlighted}),h}function c(r,u){var u;if(!r)return r;if(!u||(u=i(u.toLocaleLowerCase()),r.toLocaleLowerCase()==u))return n.startMarker+r+n.endMarker;var f=o(r),h=[],l=o(u),a=[],e=-1,c=!1;return l.forEach(function(r,u){var v,o,s,l,y;if(r.length>0&&(v=!r.match(t),c||v)){for(o=0,s=++e;s<f.length+e;++s){if(o=s%f.length,!h[o]&&(l=f[o],y=!l.match(t),(c||y)&&i(l.toLocaleLowerCase())==r)){f[o]=n.startMarker+l+n.endMarker;h[o]=!0;a[u]=!0;c=!0;break}c=!1}e=o}}),e=-1,l.forEach(function(r,u){var o,c,s,l,v;if(!a[u]&&r.length>0){for(o=0,c=++e;c<f.length+e;++c)if(o=c%f.length,!h[o]&&(s=f[o],l=!s.match(t),l&&(v=i(s.toLocaleLowerCase()).indexOf(r),v==0))){f[o]=n.startMarker+s.substr(0,r.length)+n.endMarker+s.substr(r.length);h[o]=!0;break}e=o}}),"".concat.apply("",f).replace(s,"")}function o(n){var r=[],i=0;return n.replace(t,function(t,u,f,e){return e>i&&r.push(n.substr(i,e-i)),r.push(t),i=e+t.length,t}),i<n.length&&r.push(n.substr(i,n.length-i)),r}function i(n){for(var u,f=!1,t=n.split(""),i=t.length-1;i>=0;--i)u=t[i],r.hasOwnProperty(u)&&(t[i]=r[u],f=!0);return f&&(n=t.join("")),n}var r;n.startMarker=String.fromCharCode(57344);n.endMarker=String.fromCharCode(57345);var u=new RegExp(n.startMarker,"g"),f=new RegExp(n.endMarker,"g"),s=new RegExp(n.endMarker+n.startMarker,"g"),t=new RegExp("([\\s.。?!,،\"':;¿¡/\\-()+_@=&"+String.fromCharCode(160)+"\\\\]|%(20|23|24|26|2b|2c|2f|3a|3d|3f|40))+","gi");n.removeMarkers=e;n.split=h;n.addMarkers=c;n.removeDiacritics=i;r={"ⓐ":"a","ａ":"a","ẚ":"a","à":"a","á":"a","â":"a","ầ":"a","ấ":"a","ẫ":"a","ẩ":"a","ã":"a","ā":"a","ă":"a","ằ":"a","ắ":"a","ẵ":"a","ẳ":"a","ȧ":"a","ǡ":"a","ä":"a","ǟ":"a","ả":"a","å":"a","ǻ":"a","ǎ":"a","ȁ":"a","ȃ":"a","ạ":"a","ậ":"a","ặ":"a","ḁ":"a","ą":"a","ⱥ":"a","ɐ":"a","ꜳ":"aa","æ":"ae","ǽ":"ae","ǣ":"ae","ꜵ":"ao","ꜷ":"au","ꜹ":"av","ꜻ":"av","ꜽ":"ay","ⓑ":"b","ｂ":"b","ḃ":"b","ḅ":"b","ḇ":"b","ƀ":"b","ƃ":"b","ɓ":"b","ⓒ":"c","ｃ":"c","ć":"c","ĉ":"c","ċ":"c","č":"c","ç":"c","ḉ":"c","ƈ":"c","ȼ":"c","ꜿ":"c","ↄ":"c","ⓓ":"d","ｄ":"d","ḋ":"d","ď":"d","ḍ":"d","ḑ":"d","ḓ":"d","ḏ":"d","đ":"d","ƌ":"d","ɖ":"d","ɗ":"d","ꝺ":"d","ǳ":"dz","ǆ":"dz","ⓔ":"e","ｅ":"e","è":"e","é":"e","ê":"e","ề":"e","ế":"e","ễ":"e","ể":"e","ẽ":"e","ē":"e","ḕ":"e","ḗ":"e","ĕ":"e","ė":"e","ë":"e","ẻ":"e","ě":"e","ȅ":"e","ȇ":"e","ẹ":"e","ệ":"e","ȩ":"e","ḝ":"e","ę":"e","ḙ":"e","ḛ":"e","ɇ":"e","ɛ":"e","ǝ":"e","ⓕ":"f","ｆ":"f","ḟ":"f","ƒ":"f","ꝼ":"f","ⓖ":"g","ｇ":"g","ǵ":"g","ĝ":"g","ḡ":"g","ğ":"g","ġ":"g","ǧ":"g","ģ":"g","ǥ":"g","ɠ":"g","ꞡ":"g","ᵹ":"g","ꝿ":"g","ⓗ":"h","ｈ":"h","ĥ":"h","ḣ":"h","ḧ":"h","ȟ":"h","ḥ":"h","ḩ":"h","ḫ":"h","ẖ":"h","ħ":"h","ⱨ":"h","ⱶ":"h","ɥ":"h","ƕ":"hv","ⓘ":"i","ｉ":"i","ì":"i","í":"i","î":"i","ĩ":"i","ī":"i","ĭ":"i","ï":"i","ḯ":"i","ỉ":"i","ǐ":"i","ȉ":"i","ȋ":"i","ị":"i","į":"i","ḭ":"i","ɨ":"i","ı":"i","ⓙ":"j","ｊ":"j","ĵ":"j","ǰ":"j","ɉ":"j","ⓚ":"k","ｋ":"k","ḱ":"k","ǩ":"k","ḳ":"k","ķ":"k","ḵ":"k","ƙ":"k","ⱪ":"k","ꝁ":"k","ꝃ":"k","ꝅ":"k","ꞣ":"k","ⓛ":"l","ｌ":"l","ŀ":"l","ĺ":"l","ľ":"l","ḷ":"l","ḹ":"l","ļ":"l","ḽ":"l","ḻ":"l","ł":"l","ƚ":"l","ɫ":"l","ⱡ":"l","ꝉ":"l","ꞁ":"l","ꝇ":"l","ǉ":"lj","ⓜ":"m","ｍ":"m","ḿ":"m","ṁ":"m","ṃ":"m","ɱ":"m","ɯ":"m","ⓝ":"n","ｎ":"n","ǹ":"n","ń":"n","ñ":"n","ṅ":"n","ň":"n","ṇ":"n","ņ":"n","ṋ":"n","ṉ":"n","ƞ":"n","ɲ":"n","ŉ":"n","ꞑ":"n","ꞥ":"n","ǌ":"nj","ⓞ":"o","ｏ":"o","ò":"o","ó":"o","ô":"o","ồ":"o","ố":"o","ỗ":"o","ổ":"o","õ":"o","ṍ":"o","ȭ":"o","ṏ":"o","ō":"o","ṑ":"o","ṓ":"o","ŏ":"o","ȯ":"o","ȱ":"o","ö":"o","ȫ":"o","ỏ":"o","ő":"o","ǒ":"o","ȍ":"o","ȏ":"o","ơ":"o","ờ":"o","ớ":"o","ỡ":"o","ở":"o","ợ":"o","ọ":"o","ộ":"o","ǫ":"o","ǭ":"o","ø":"o","ǿ":"o","ɔ":"o","ꝋ":"o","ꝍ":"o","ɵ":"o","œ":"oe","ɶ":"oe","ƣ":"oi","ȣ":"ou","ꝏ":"oo","ⓟ":"p","ｐ":"p","ṕ":"p","ṗ":"p","ƥ":"p","ᵽ":"p","ꝑ":"p","ꝓ":"p","ꝕ":"p","ⓠ":"q","ｑ":"q","ɋ":"q","ꝗ":"q","ꝙ":"q","ⓡ":"r","ｒ":"r","ŕ":"r","ṙ":"r","ř":"r","ȑ":"r","ȓ":"r","ṛ":"r","ṝ":"r","ŗ":"r","ṟ":"r","ɍ":"r","ɽ":"r","ꝛ":"r","ꞧ":"r","ꞃ":"r","ⓢ":"s","ｓ":"s","ś":"s","ṥ":"s","ŝ":"s","ṡ":"s","š":"s","ṧ":"s","ṣ":"s","ṩ":"s","ș":"s","ş":"s","ȿ":"s","ꞩ":"s","ꞅ":"s","ſ":"s","ẛ":"s","ß":"ss","ⓣ":"t","ｔ":"t","ṫ":"t","ẗ":"t","ť":"t","ṭ":"t","ț":"t","ţ":"t","ṱ":"t","ṯ":"t","ŧ":"t","ƭ":"t","ʈ":"t","ⱦ":"t","ꞇ":"t","ꜩ":"tz","ⓤ":"u","ｕ":"u","ù":"u","ú":"u","û":"u","ũ":"u","ṹ":"u","ū":"u","ṻ":"u","ŭ":"u","ü":"u","ǜ":"u","ǘ":"u","ǖ":"u","ǚ":"u","ủ":"u","ů":"u","ű":"u","ǔ":"u","ȕ":"u","ȗ":"u","ư":"u","ừ":"u","ứ":"u","ữ":"u","ử":"u","ự":"u","ụ":"u","ṳ":"u","ų":"u","ṷ":"u","ṵ":"u","ʉ":"u","ⓥ":"v","ｖ":"v","ṽ":"v","ṿ":"v","ʋ":"v","ꝟ":"v","ʌ":"v","ꝡ":"vy","ⓦ":"w","ｗ":"w","ẁ":"w","ẃ":"w","ŵ":"w","ẇ":"w","ẅ":"w","ẘ":"w","ẉ":"w","ⱳ":"w","ⓧ":"x","ｘ":"x","ẋ":"x","ẍ":"x","ⓨ":"y","ｙ":"y","ỳ":"y","ý":"y","ŷ":"y","ỹ":"y","ȳ":"y","ẏ":"y","ÿ":"y","ỷ":"y","ẙ":"y","ỵ":"y","ƴ":"y","ɏ":"y","ỿ":"y","ⓩ":"z","ｚ":"z","ź":"z","ẑ":"z","ż":"z","ž":"z","ẓ":"z","ẕ":"z","ƶ":"z","ȥ":"z","ɀ":"z","ⱬ":"z","ꝣ":"z","０":"0","₀":"0","⓪":"0","⁰":"0","¹":"1","⑴":"1","₁":"1","