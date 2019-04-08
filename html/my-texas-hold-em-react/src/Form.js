import React, { Component } from "react";

class Form extends Component {
  constructor(props) {
    super(props);

    this.initialState = {
      name: "",
      numberOfOpponents: ""
    };

    this.state = this.initialState;
  }

  handleChange = event => {
    const { name, value } = event.target;

    this.setState({
      [name]: value
    });
  };

  onFormSubmit = event => {
    event.preventDefault();

    this.props.handleSubmit(this.state);
    this.setState(this.initialState);
  };

  render() {
    const { name, numberOfOpponents } = this.state;

    return (
      <form onSubmit={this.onFormSubmit}>
        <label>Name</label>
        <input
          type="text"
          name="name"
          value={name}
          onChange={this.handleChange}
        />
        <label>numberOfOpponents</label>
        <input
          type="text"
          name="number of opponents"
          value={numberOfOpponents}
          onChange={this.handleChange}
        />
        <label>numberOfOpponents2</label>
        <input
          type="text"
          name="number of opponents"
          value={numberOfOpponents}
          onChange={this.handleChange}
        />
        <button type="submit">Play</button>
      </form>
    );
  }
}

export default Form;
