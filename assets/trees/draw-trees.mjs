// Two independently composed vector trees. Coordinates are authored here;
// no geometry from the oak reference is transformed or reused.
const root = new URL('../..', import.meta.url).pathname;
function crown(id, points, light, mid, dark) {
  // Leaf-sized notches follow the authored outline, rather than scattered leaves.
  let d = `M${points[0][0]} ${points[0][1]}`;
  for (let i=0;i<points.length;i++) {
    const a=points[i], b=points[(i+1)%points.length];
    const dx=b[0]-a[0],dy=b[1]-a[1],len=Math.hypot(dx,dy);
    const n=Math.max(2,Math.round(len/6));
    for(let j=1;j<=n;j++){
      const t=j/n, bend=[-5,2,-3,5,-2][(i+j)%5];
      d+=` L${(a[0]+dx*(t-.45/n)+dy/len*bend).toFixed(1)} ${(a[1]+dy*(t-.45/n)-dx/len*bend).toFixed(1)} L${(a[0]+dx*t).toFixed(1)} ${(a[1]+dy*t).toFixed(1)}`;
    }
  }
  d+='Z';
  const minX=Math.min(...points.map(p=>p[0])),maxX=Math.max(...points.map(p=>p[0]));
  const minY=Math.min(...points.map(p=>p[1])),maxY=Math.max(...points.map(p=>p[1]));
  const w=maxX-minX,h=maxY-minY;
  const accents=Array.from({length:12},(_,i)=>{
    const x=minX+w*(.12+((i*7)%11)/14), y=minY+h*(.22+((i*3)%7)/13);
    return `<path d="M${x} ${y}l4-5 6 2 3-4 5 3-4 5-7-1-3 3Z" fill="${i%3===0?light:dark}" opacity=".48"/>`;
  }).join("");
  return `<g><defs><clipPath id="${id}"><path d="${d}"/></clipPath></defs>
  <path d="${d}" fill="${mid}" stroke="#283e2e" stroke-width="2.5" stroke-linejoin="round"/>
  <g clip-path="url(#${id})">
  <path d="M${minX-4} ${minY-4}H${maxX+4}V${minY+h*.36}l-17-7-9 12-19-6-8 13-19-8-14 15-22-11-10 13-24-7-14 12-22-10-11 8Z" fill="${light}"/>
  <path d="M${minX-4} ${minY+h*.64}l15 8 11-5 17 14 13-8 18 12 12-6 19 9 14-13 21 6 13-10 17 5 14-8 20 6V${maxY+8}H${minX-4}Z" fill="${dark}"/>
  ${accents}</g></g>`;
}
const bark = '#94623c', outline='#3f3428';
const a=[];
a.push(crown('arch-back',[[45,180],[28,140],[43,104],[89,82],[126,104],[154,142],[142,187],[94,214]],'#758d50','#466541','#2c4934'));
a.push(crown('arch-far',[[200,160],[194,104],[225,77],[272,82],[304,108],[311,145],[281,182],[237,185]],'#7f9856','#4f7144','#314d36'));
a.push(`<path d="M117 392Q141 346 137 306Q135 270 114 243L71 217L48 187L56 177L87 204L119 215L126 170L115 137L124 132L145 170L143 230Q159 249 170 230L201 187L214 145L225 130L231 135L222 171L253 153L280 152L278 160L243 170L216 191L186 241Q171 269 175 305Q173 351 197 393L177 389L162 397L148 391L131 396Z" fill="${bark}" stroke="${outline}" stroke-width="3"/>
<path d="M137 387Q158 333 151 295Q145 261 135 246L103 220L128 230L139 196L143 239Q154 268 162 249L202 196L219 173L204 204L177 247Q158 286 163 315L165 380Z" fill="#b68754"/>
<path d="M149 380Q136 330 151 306M172 376Q159 342 168 316M150 283L139 255M118 236L87 215M179 251L199 221M217 183L243 165" fill="none" stroke="#63452d" stroke-width="2.4"/>`);
a.push(crown('arch-left',[[8,170],[5,135],[21,111],[66,98],[100,110],[112,139],[91,163],[51,183],[20,184]],'#91a865','#577b49','#354f37'));
a.push(crown('arch-top',[[66,109],[51,77],[70,52],[99,46],[105,25],[143,14],[176,28],[206,20],[235,43],[240,75],[215,104],[181,114],[145,102],[111,122]],'#9aae6a','#63854d','#3f603c'));
a.push(crown('arch-right',[[211,145],[227,117],[263,110],[277,92],[307,105],[337,127],[345,156],[327,178],[296,181],[276,168],[247,177]],'#8ea05c','#567745','#314d36'));
a.push(crown('arch-low',[[68,217],[84,188],[121,183],[145,204],[140,230],[115,251],[78,246],[56,233]],'#7c9452','#4c6c40','#2d4731'));

const b=[];
b.push(crown('fork-back',[[74,213],[55,178],[65,145],[86,137],[99,110],[129,112],[153,139],[156,185],[124,222]],'#789451','#486c41','#2c4c35'));
b.push(crown('fork-right-back',[[186,177],[172,146],[188,118],[222,109],[253,130],[262,163],[239,189],[209,192]],'#819b59','#517649','#314e36'));
b.push(`<path d="M128 395L144 366L149 313L143 272L128 240L104 222L85 193L93 185L119 211L144 230L147 195L131 164L133 132L142 126L145 158L162 184L177 157L179 120L191 92L201 87L205 94L191 124L192 155L184 189L187 231L207 211L232 179L246 168L252 172L236 194L222 224L189 253L181 296L187 348L205 395L185 388L174 398L157 391L140 398Z" fill="${bark}" stroke="${outline}" stroke-width="3"/>
<path d="M147 384L159 330L157 286L154 254L133 228L159 240L165 215L162 190L180 169L175 202L177 245L201 224L180 255L170 288L175 341L185 384L171 376L161 386Z" fill="#c0925b"/>
<path d="M161 368L166 326L161 305M182 363L176 342M157 275L159 251M174 242L169 218M183 177L188 146M208 226L220 212M148 246L134 234" fill="none" stroke="#60432d" stroke-width="2.3"/>`);
b.push(crown('fork-high',[[110,116],[98,88],[110,61],[131,49],[131,24],[154,7],[181,15],[192,38],[212,46],[223,76],[210,103],[182,123],[157,114],[135,132]],'#a2b877','#6c9458','#436a43'));
b.push(crown('fork-middle',[[155,176],[151,150],[171,130],[192,135],[209,115],[239,113],[260,135],[263,160],[244,179],[210,175],[186,190]],'#95ae6a','#5f8851','#365a3c'));
b.push(crown('fork-left',[[33,228],[21,199],[31,172],[55,157],[82,165],[99,186],[94,211],[71,231],[48,239]],'#8ea865','#577f4d','#33543b'));
b.push(crown('fork-low',[[193,259],[190,233],[209,215],[235,217],[253,203],[278,221],[283,246],[266,268],[238,272],[217,261]],'#8da465','#557c4c','#2f5138'));

const trees=[['arch-oak',a,'0 0 355 405'],['forked-tree',b,'0 0 310 405']];
let symbols='';
for(const [name,parts,viewBox] of trees){
 const body=parts.join('\n');
 await Bun.write(root+`/assets/trees/${name}.svg`,`<svg xmlns="http://www.w3.org/2000/svg" viewBox="${viewBox}" role="img"><title>${name}</title>${body}</svg>`);
 symbols+=`<symbol id="${name}" viewBox="${viewBox}" overflow="visible">${body}</symbol>\n`;
}
await Bun.write(root+'/resources/trees/foreground.svg',symbols);
