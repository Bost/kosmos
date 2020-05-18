import * as p5 from "p5";
import { Store } from "redux";

import { Point, distance } from "./geometry";
import { Atom } from "../store/atom";
import AtomShape from "./atom_shape";
import * as Legend from "./legend";
import { gridPoints, gridTiles } from "./grid";
import * as ViewField from "./view_field";
import * as ValueInput from "./value_input";
import { moveAtom } from "../store";
import { edgesSelector, atomsSelector, moveCanvas } from "../store";
import { actions } from "../store";

export default function Sketch(store: Store) {
  return (p: p5) => {
    const HOLD_DURATION = 750;
    const HOLD_DIST_THRESHOLD = 20;

    let timestamp: number = undefined;
    let startPoint: Point = undefined;
    let keepDrawings = false;
    let showFPS = false;

    let viewField: ViewField.ViewField;

    const backgroundColor = p.color("#FDFDFD");
    let bg: p5.Graphics = null;

    function findMouseOverAtom(atoms: Atom[], mouse: Point) {
      return atoms.find(atom => AtomShape.within(mouse, atom));
    }

    const mousePosition =
      () : Point => {
        const mouse = { x: p.mouseX, y: p.mouseY };
        return ViewField.toGlobalCoordinates(viewField, mouse);
      };

    function drawEdges() {
      p.push();

      p.strokeWeight(1.5);
      p.stroke(150);

      edgesSelector(store).forEach(e => p.line(e.x1, e.y1, e.x2, e.y2));

      p.pop();
    }

    function drawAtoms() {
      p.push();

      const atoms: Atom[] = atomsSelector(store);
      atoms.forEach(atom => {
        const selected = store.getState().selectedAtomId == atom.id;
        AtomShape.draw(p, atom, selected);
      });

      p.pop();
    }

    function drawHoldAnimation() {
      if (!startPoint) return;

      const f = () => {
        const currentPoint = mousePosition();
        const dist = distance(startPoint, currentPoint);
        if (dist >= HOLD_DIST_THRESHOLD) return;

        const now = new Date().getTime();
        const mousePressedDuration = now - timestamp;
        if (mousePressedDuration < 250) return;

        let angleRad =  p.TWO_PI;

        const r = mousePressedDuration / HOLD_DURATION;
        if (r < 1) {
          angleRad = angleRad * r;
        }

        let radius = 20;
        let { x, y } = startPoint;

        const atom = findMouseOverAtom(atomsSelector(store), currentPoint);
        if (atom) {
          x = atom.x;
          y = atom.y;
        }

        p.push();
        p.stroke(0, 0, 0, 50);
        p.noFill();
        p.strokeCap(p.PROJECT);
        p.strokeWeight(6);
        p.arc(x, y, radius, radius, 0, angleRad);
        p.pop();
      }
      f();
    }

    function drawBackground(s: p5, bg: p5.Graphics, color: p5.Color) {
      const renderPoint = ({x, y}: Point) => bg.point(x, y);

      bg.background(color);
      bg.stroke(210);
      bg.strokeWeight(3);

      gridPoints(bg.width, bg.height).forEach(renderPoint);

      return bg;
    }

    function drawFPS() {
      if (!showFPS) return;

      const fps = p.frameRate();
      const { x, y } = ViewField.toGlobalCoordinates(viewField, { x: 10, y: 20 });

      p.push();
      p.textSize(14);
      p.fill(0);
      p.stroke(0);
      p.text("FPS: " + fps.toFixed(2), x, y);
      p.pop();
    }

    const drawLegend = () => {
      const content = Legend.text(store);
      const { x, y } =
        ViewField.toGlobalCoordinates(viewField, { x: 20, y: p.height - 30 });

      p.push();
      {
        p.fill(50);
        p.strokeWeight(0);
        p.textAlign(p.LEFT, p.CENTER);
        p.textFont("monospace", 13);
        p.text(content, x, y);
      }
      p.pop();
    };

    p.setup = () => {
      p.pixelDensity(2);
      p.frameRate(30);
      p.noLoop();

      viewField = ViewField.init(p.windowWidth, p.windowHeight);

      p.createCanvas(p.windowWidth, p.windowHeight);
      bg = p.createGraphics(1000, 1000);
      bg = drawBackground(p, bg, backgroundColor);

      ValueInput.setup(p, store);
    };

    p.draw = () => {
      const { x, y } = store.getState().canvasTranslate;
      p.translate(x, y);

      ValueInput.update(p, store);

      const tiles = gridTiles(viewField, bg.width, bg.height);
      tiles.forEach(({ x, y, width, height}) => p.image(bg, x, y, width, height));

      drawEdges();
      drawAtoms();
      drawHoldAnimation();
      drawLegend();
      drawFPS();
    };

    p.windowResized = () => {
      viewField = ViewField.resize(viewField, p.windowWidth, p.windowHeight);
      p.resizeCanvas(p.windowWidth, p.windowHeight);
    };

    p.mousePressed = (event: MouseEvent) => {
      const tagName = (event.srcElement as HTMLElement).tagName;
      if (tagName !== "CANVAS" && tagName !== "INPUT") return;

      store.dispatch(actions.canvasMousePressed(p.mouseX, p.mouseY));
    }

    p.mouseReleased = (event: MouseEvent) => {
      const tagName = (event.srcElement as HTMLElement).tagName;
      if (tagName !== "CANVAS" && tagName !== "INPUT") return;

      store.dispatch(actions.canvasMouseReleased(p.mouseX, p.mouseY));
    }

    p.mouseClicked = () => {
      store.dispatch(actions.canvasClicked(p.mouseX, p.mouseY));
    }

    p.doubleClicked = () => {
      store.dispatch(actions.canvasDoubleClicked(p.mouseX, p.mouseY));
    }

    p.mouseMoved = () => {
    }

    p.mouseWheel = (event: WheelEvent) => {
      viewField = ViewField.move(viewField, event.deltaX, event.deltaY);
      const { x, y } = ViewField.translateTo(viewField);
      store.dispatch(moveCanvas(x, y));
    }

    p.mouseDragged = () => {
      const draggingAtomId = store.getState().draggingAtomId;
      if (draggingAtomId) {
        const { x, y } = mousePosition();
        store.dispatch(moveAtom(draggingAtomId, x, y));
      }
    }

    p.keyReleased = () => {
      const atom = store.getState().selectedAtom;

      if (p.key == "d" && !atom) {
        keepDrawings = !keepDrawings;
      }

      if (p.key == "f" && !atom) {
        showFPS = !showFPS;
      }
    }
  }
}
