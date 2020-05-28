import { createApplicationStore } from "./store";
import Keyboard from "./keyboard";
import AppMenu from "./app_menu";
import Window from "./window";
import Interface from "./interface";
import Canvas from "./canvas";
import Repl from "./repl";
import "./workspace";
import { start } from "./core/actor";

const store = createApplicationStore();

const StoreBehavior = {
  dispatch: (state, action) => {
    store.dispatch(action);
    return state;
  },
  getState: (state) => ({ state, response: store.getState() }),
};
start(StoreBehavior, "store");

Window(store);
AppMenu(store);
Keyboard(store);
Canvas(store);
Interface(store);
Repl(store);
