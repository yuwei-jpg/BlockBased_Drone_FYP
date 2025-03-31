"use strict";
(function(module){if(typeof define==='function'&&define.amd){define(['exports'],function(exports){module(exports);});}else if(typeof exports==='object'&&exports!==null&&typeof exports.nodeName!=='string'){module(exports);}else{module(typeof self!=='undefined'?self:this);}}(function(B){let Bq=2463534242,BD=()=>{let x=Bq;x^=x<<13;x^=x>>>17;x^=x<<5;Bq=x;return x;},BJ=f=>(args,callback)=>{if(!args){args=[];}let javaArgs=Z(H(),args.length);for(let i=0;i<args.length;++i){javaArgs.data[i]=J(args[i]);}BT(()=>{f.call(null,
javaArgs);},callback);},B6=target=>target.$clinit=()=>{},B1=obj=>{let cls=obj.constructor;let arrayDegree=0;while(cls.$meta&&cls.$meta.item){++arrayDegree;cls=cls.$meta.item;}let clsName="";if(cls.$meta.primitive){clsName=cls.$meta.name;}else {clsName=cls.$meta?cls.$meta.name||"a/"+cls.name:"@"+cls.name;}while(arrayDegree-->0){clsName+="[]";}return clsName;},E=superclass=>{if(superclass===0){return function(){};}if(superclass===void 0){superclass=H();}return function(){superclass.call(this);};},Bg=cls=>BH(cls),H
=()=>D,B3=()=>{return {$array:null,classObject:null,$meta:{supertypes:[],superclass:null}};},Bt=(name,binaryName)=>{let cls=B3();cls.$meta.primitive=true;cls.$meta.name=name;cls.$meta.binaryName=binaryName;cls.$meta.enum=false;cls.$meta.item=null;cls.$meta.simpleName=null;cls.$meta.declaringClass=null;cls.$meta.enclosingClass=null;return cls;},Bh=Bt("char","C"),Ca=Bt("int","I");
if(typeof BigInt!=='function'){}else if(typeof BigInt64Array!=='function'){}else {}let Z=(cls,sz)=>{let data=new Array(sz);data.fill(null);return new (S(cls))(data);};
if(typeof BigInt64Array!=='function'){}else {}let M=sz=>new BZ(new Uint16Array(sz)),S=cls=>{let result=cls.$array;if(result===null){function JavaArray(data){(H()).call(this);this.data=data;}JavaArray.prototype=Object.create((H()).prototype);JavaArray.prototype.type=cls;JavaArray.prototype.constructor=JavaArray;JavaArray.prototype.toString=function(){let str="[";for(let i=0;i<this.data.length;++i){if(i>0){str+=", ";}str+=this.data[i].toString();}str+="]";return str;};JavaArray.prototype.o=function(){let dataCopy;if
('slice' in this.data){dataCopy=this.data.slice();}else {dataCopy=new this.data.constructor(this.data.length);for(let i=0;i<dataCopy.length;++i){dataCopy[i]=this.data[i];}}return new (S(this.type))(dataCopy);};let name="["+cls.$meta.binaryName;JavaArray.$meta={item:cls,supertypes:[H()],primitive:false,superclass:H(),name:name,binaryName:name,enum:false,simpleName:null,declaringClass:null,enclosingClass:null};JavaArray.classObject=null;JavaArray.$array=null;result=JavaArray;cls.$array=JavaArray;}return result;},W,BQ
=strings=>{Ck();W=new Array(strings.length);for(let i=0;i<strings.length;++i){W[i]=Bl(J(strings[i]));}},N=index=>W[index],Bk=(array,offset,count)=>{let result="";let limit=offset+count;for(let i=offset;i<limit;i=i+1024|0){let next=Math.min(limit,i+1024|0);result+=String.fromCharCode.apply(null,array.subarray(i,next));}return result;},J=str=>str===null?null:Cd(str),Bw=str=>str===null?null:str.g,Ck=()=>(()=>{})(),Bl;
{Bl=str=>str;}let R=ex=>{throw Cl(ex);},P=Symbol("javaException"),Cl=ex=>{let err=ex.$jsException;if(!err){let javaCause=Cf(ex);let jsCause=javaCause!==null?javaCause.$jsException:void 0;let cause=typeof jsCause==="object"?{cause:jsCause}:void 0;err=new F("Java exception thrown",cause);if(typeof Error.captureStackTrace==="function"){Error.captureStackTrace(err);}err[P]=ex;ex.$jsException=err;Ch(err,ex);}return err;},Ch=(err,ex)=>{if(typeof B9==="function"&&err.stack){let stack=B9(err.stack);let javaStack=Z(BK(),
stack.length);let elem;let noStack=false;for(let i=0;i<stack.length;++i){let element=stack[i];elem=BV(J(element.className),J(element.methodName),J(element.fileName),element.lineNumber);if(elem==null){noStack=true;break;}javaStack.data[i]=elem;}if(!noStack){B$(ex,javaStack);}}},F;
if(typeof Reflect==='object'){let defaultMessage=Symbol("defaultMessage");F=function F(message,cause){let self=Reflect.construct(Error,[void 0,cause],F);Object.setPrototypeOf(self,F.prototype);self[defaultMessage]=message;return self;}
;F.prototype=Object.create(Error.prototype,{constructor:{configurable:true,writable:true,value:F},message:{get(){try {let javaException=this[P];if(typeof javaException==='object'){let javaMessage=Bz(javaException);if(typeof javaMessage==="object"){return javaMessage!==null?javaMessage.toString():null;}}return this[defaultMessage];}catch(e){return "Exception occurred trying to extract Java exception message: "+e;}}}});}else {F=Error;}let BP=e=>e instanceof Error&&typeof e[P]==='object'?e[P]:null,Bz=t=>Cb(t),Cf
=t=>Ci(t),BK=()=>H(),BV=(className,methodName,fileName,lineNumber)=>{{return null;}},B$=(e,stack)=>{},Bv=null,BB=data=>{let i=0;let packages=new Array(data.length);for(let j=0;j<data.length;++j){let prefixIndex=data[i++];let prefix=prefixIndex>=0?packages[prefixIndex]:"";packages[j]=prefix+data[i++]+".";}Bv=packages;},Ce=data=>{let packages=Bv;let i=0;while(i<data.length){let cls=data[i++];cls.$meta={};let m=cls.$meta;let className=data[i++];m.name=className!==0?className:null;if(m.name!==null){let packageIndex
=data[i++];if(packageIndex>=0){m.name=packages[packageIndex]+m.name;}}m.binaryName="L"+m.name+";";let superclass=data[i++];m.superclass=superclass!==0?superclass:null;m.supertypes=data[i++];if(m.superclass){m.supertypes.push(m.superclass);cls.prototype=Object.create(m.superclass.prototype);}else {cls.prototype={};}let flags=data[i++];m.enum=(flags&8)!==0;m.flags=flags;m.primitive=false;m.item=null;cls.prototype.constructor=cls;cls.classObject=null;m.accessLevel=data[i++];let innerClassInfo=data[i++];if(innerClassInfo
===0){m.simpleName=null;m.declaringClass=null;m.enclosingClass=null;}else {let enclosingClass=innerClassInfo[0];m.enclosingClass=enclosingClass!==0?enclosingClass:null;let declaringClass=innerClassInfo[1];m.declaringClass=declaringClass!==0?declaringClass:null;let simpleName=innerClassInfo[2];m.simpleName=simpleName!==0?simpleName:null;}let clinit=data[i++];cls.$clinit=clinit!==0?clinit:function(){};let virtualMethods=data[i++];if(virtualMethods!==0){for(let j=0;j<virtualMethods.length;j+=2){let name=virtualMethods[j];let func
=virtualMethods[j+1];if(typeof name==='string'){name=[name];}for(let k=0;k<name.length;++k){cls.prototype[name[k]]=func;}}}cls.$array=null;}},BT=(runner,callback)=>{let result;try {result=runner();}catch(e){result=e;}if(typeof callback!=='undefined'){callback(result);}else if(result instanceof Error){throw result;}};
function D(){this.$id$=0;}
let B4=a=>{let b,c,d,e,f,g,h,i,j,k,l,m;b=a;if(!b.$id$)b.$id$=BD();c=a.$id$;if(!c)d=N(0);else{if(!c)e=32;else{f=0;e=c>>>16|0;if(e)f=16;else e=c;g=e>>>8|0;if(!g)g=e;else f=f|8;e=g>>>4|0;if(!e)e=g;else f=f|4;g=e>>>2|0;if(!g)g=e;else f=f|2;if(g>>>1|0)f=f|1;e=(32-f|0)-1|0;}h=(((32-e|0)+4|0)-1|0)/4|0;i=M(h);j=i.data;h=(h-1|0)*4|0;e=0;while(h>=0){f=e+1|0;g=(c>>>h|0)&15;j[e]=g>=0&&g<16?(g<10?(48+g|0)&65535:((97+g|0)-10|0)&65535):0;h=h-4|0;e=f;}d=B5(i);}b=new Be;b.h=M(16);Bp(Bp(b,N(1)),d);k=new G;i=b.h;j=i.data;l=b.i;m
=j.length;if(l>=0&&l<=(m-0|0)){k.g=Bk(i.data,0,l);return k;}R(BU());},Bd=E(0),Bs=E(0);
function Br(){D.call(this);this.n=null;}
let BH=b=>{let c;if(b===null)return null;c=b.classObject;if(c===null){c=new Br;c.n=b;b.classObject=c;}return c;},Cn=E(),B2=E();
function U(){let a=this;D.call(a);a.l=null;a.m=null;a.k=0;a.j=0;}
let Cq=a=>{return a;},Cb=a=>{return a.l;},Ci=a=>{let b;b=a.m;if(b===a)b=null;return b;},Ba=E(U),I=E(Ba),BX=(a,b)=>{a.k=1;a.j=1;a.l=b;},Cp=a=>{let b=new I();BX(b,a);return b;},Cg=E(I),K=E(0),L=E(0),Q=E(0),G=E(),BN=null,B0=null,BE=null,BL=(a,b)=>{a.g=Bk(b.data,0,b.data.length);},B5=a=>{let b=new G();BL(b,a);return b;},BA=(a,b)=>{a.g=b;},Cd=a=>{let b=new G();BA(b,a);return b;},BM=()=>{let b;BN=M(0);b=new G;b.g="";B0=b;BE=new Bf;},X=E(),BO=E(X),BS=null,BC=()=>{BS=Bg(Ca);};
function V(){let a=this;D.call(a);a.h=null;a.i=0;}
let BY=(a,b)=>{let c,d,e,f,g;c=a.h.data.length;if(c>=b)return;d=c>=1073741823?2147483647:Bi(b,Bi(c*2|0,5));e=a.h.data;f=M(d);b=e.length;if(d<b)b=d;g=f.data;c=0;while(c<b){g[c]=e[c];c=c+1|0;}a.h=f;},By=E(0),Be=E(V),Bp=(a,b)=>{let c,d,e,f,g,h,i;c=a.i;d=a;b=b===null?N(2):b;e=d;if(c>=0&&c<=e.i){a:{b:{if(b===null)b=N(2);else if(b.g.length?0:1)break b;f=e.i+b.g.length|0;BY(e,f);g=e.i-1|0;while(g>=c){e.h.data[g+b.g.length|0]=e.h.data[g];g=g+(-1)|0;}e.i=e.i+b.g.length|0;f=0;while(f<b.g.length){h=e.h;i=c+1|0;if(f<0)break a;if
(f>=b.g.length)break a;h.data[c]=b.g.charCodeAt(f);f=f+1|0;c=i;}}return a;}R(BW());}b=new Y;Bb(b);R(b);},Bx=E(),Bc=()=>{Bc=B6(Bx);Cj();},BI=b=>{let c,d,e;Bc();c=window.document;d=c.createElement("div");e=c.createTextNode("TeaVM generated element");d.appendChild(e);c.body.appendChild(d);},Cj=()=>{BM();BC();Cc();},B_=E(),T=E(0),Bj=E(0),Bm=E(0),Bo=E(0),BF=E(),Bn=E(0),Bf=E(),Bu=E(),BR=null,B7=null,Cc=()=>{BR=Bg(Bh);B7=Z(Bu,128);},BG=E(),O=E(I),Bb=a=>{a.k=1;a.j=1;},BU=()=>{let a=new O();Bb(a);return a;},Y=E(O),Co
=a=>{Bb(a);},BW=()=>{let a=new Y();Co(a);return a;},B8=E(),Bi=(b,c)=>{if(b>c)c=b;return c;},Cm=E();
BB([]);
Ce([D,0,0,[],0,3,0,0,0,Bd,0,D,[],3,3,0,0,0,Bs,0,D,[],3,3,0,0,0,Br,0,D,[Bd,Bs],4,3,0,0,0,Cn,0,D,[],4,3,0,0,0,B2,0,D,[],4,3,0,0,0,U,0,D,[],0,3,0,0,0,Ba,0,U,[],0,3,0,0,0,I,0,Ba,[],0,3,0,0,0,Cg,0,I,[],0,3,0,0,0,K,0,D,[],3,3,0,0,0,L,0,D,[],3,3,0,0,0,Q,0,D,[],3,3,0,0,0,G,0,D,[K,L,Q],0,3,0,0,0,X,0,D,[K],1,3,0,0,0,BO,0,X,[L],0,3,0,0,0,V,0,D,[K,Q],0,0,0,0,0,By,0,D,[],3,3,0,0,0,Be,0,V,[By],0,3,0,0,0,Bx,0,D,[],0,3,0,Bc,0,B_,0,D,[],4,3,0,0,0,T,0,D,[],3,3,0,0,0,Bj,0,D,[T],3,3,0,0,0,Bm,0,D,[Bj],3,3,0,0,0,Bo,0,D,[T],3,3,0,
0,0,BF,0,D,[Bm,Bo],1,3,0,0,0,Bn,0,D,[],3,3,0,0,0,Bf,0,D,[Bn],0,3,0,0,0,Bu,0,D,[L],0,3,0,0,0,BG,0,D,[],4,3,0,0,0,O,0,I,[],0,3,0,0,0,Y,0,O,[],0,3,0,0,0,B8,0,D,[],4,3,0,0,0,Cm,0,D,[],0,3,0,0,0]);
let BZ=S(Bh);
BQ(["0","<java_object>@","null"]);
G.prototype.toString=function(){return Bw(this);};
G.prototype.valueOf=G.prototype.toString;D.prototype.toString=function(){return Bw(B4(this));};
D.prototype.__teavm_class__=function(){return B1(this);};
let C=BJ(BI);
C.javaException=BP;
B.main=C;}));

//# sourceMappingURL=classes.js.map