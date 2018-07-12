import React from 'react';
import ReactDOM from 'react-dom';
import Root from './Root';
import registerServiceWorker from './registerServiceWorker';
import { AppContainer } from 'react-hot-loader';
import './index.css';

ReactDOM.render(<Root />, document.getElementById('root'));
registerServiceWorker();

// const render = Component => {
//   ReactDOM.render(
//     <AppContainer>
//       <Component />
//     </AppContainer>,
//     document.getElementById('root')
//   )
// }

// render(Root)


// if (module.hot) {
//   module.hot.accept('./Root', () => { render(Root) })
// }


// registerServiceWorker();