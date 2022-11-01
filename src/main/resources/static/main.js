const { Button, Form, Input, Card, Toast, Divider } = antd;

const Home = () => {
  const query = new URLSearchParams(location.search);
  const [openConversationId] = React.useState(() => {
    return query.get('openConversationId') || '';
  });

  const [userInfo, setUserInfo] = React.useState({});
  const [form] = Form.useForm();

  React.useEffect(() => {
    // 获取免登授权码 https://open.dingtalk.com/document/orgapp-client/obtain-the-micro-application-logon-free-authorization-code
    dd.runtime.permission
      .requestAuthCode({
        corpId: query.get('corpId') || query.get('corpid') || '', // 企业id
      })
      .then((info) => {
        console.log('info', info);
        const code = info.code; // 通过该免登授权码可以获取用户身份
        axios
          .get('/api/getUserInfo', {
            params: {
              requestAuthCode: code,
            },
          })
          .then((result) => {
            return result.data;
          })
          .then((res) => {
            console.log(res.data);
            setUserInfo(res.data);
          });
      })
      .catch((err) => {
        console.error('获取授权码失败：' + err);
      });
  }, []);

  const handleSubmit = React.useCallback(async () => {
    const { title } = form.getFieldsValue();
    axios
      .post('/api/sendText', {
        txt: title,
        openConversationId,
      })
      .then((res) => {
        Toast.success({ content: '发送成功' });
      })
      .catch((err) => {
        Toast.fail({ content: err.message });
      });
  }, [openConversationId]);

  const sendTopCard = React.useCallback(async () => {
    axios
      .post('/api/sendTopCard', {
        openConversationId,
      })
      .then((res) => {
        Toast.success({ content: '发送群吊顶卡片成功' });
      })
      .catch((err) => {
        Toast.fail({ content: err.message });
      });
  }, []);

  const sendMessageCard = React.useCallback(async () => {
    axios
      .post('/api/sendMessageCard', {
        txt: '',
        openConversationId,
      })
      .then((res) => {
        Toast.success({ content: '发送互动卡片成功' });
      })
      .catch((err) => {
        Toast.fail({ content: err.message });
      });
  }, []);

  return (
    <div className="page-container">
      <div className="top">
        <img
          width={100}
          height={105}
          style={{ marginBottom: '16px' }}
          src="https://img.alicdn.com/imgextra/i1/O1CN01afsSQZ1IYXEH4wMGH_!!6000000000905-2-tps-200-210.png"
          alt=""
        />
        <h2>恭喜，本地服务启动成功！</h2>
        <p className="sub-title">
          当前服务地址：http://127.0.0.1:7001/index.html
        </p>
        <p className="sub-title">点击下方模拟发送体验卡片效果</p>
        <Button target="_black" href="https://open.dingtalk.com/document/org/cool-application-overview" type="link">酷应用开发指南</Button>
      </div>
      <Card title={'已接入钉钉免登录'}>
        <div className="user-card">
          <img
            src={
              userInfo?.avatar ||
              'https://img.alicdn.com/imgextra/i2/O1CN01dnoCMI21uCfm2PmmU_!!6000000007044-55-tps-93-93.svg'
            }
            className="logo"
          />
          <div>
            <h3>你好，{userInfo?.name}</h3>
          </div>
        </div>
      </Card>
      <Card title={'发送文本信息'}>
        <Form name="basic" form={form} onFinish={handleSubmit}>
          <Form.Item name="title" rules={[{ required: false }]}>
            <Input placeholder="随便输入点什么" />
          </Form.Item>
          <Button htmlType="submit" type="primary" block>
            试一试
          </Button>
        </Form>
      </Card>
      <Card title={'发送消息卡片'}>
        <div>
          <img
            className="img"
            src="https://img.alicdn.com/imgextra/i4/O1CN01j80ZsB1TRd1rTIx8s_!!6000000002379-0-tps-770-784.jpg"
            alt=""
          />
        </div>
        <Divider></Divider>
        <Button type="primary" onClick={sendMessageCard} block>
          试一试
        </Button>
      </Card>
      <Card title={'发送吊顶卡片'}>
        <div>
          <img
            className="img"
            src="https://img.alicdn.com/imgextra/i3/O1CN014zvETC1qZd71adxQX_!!6000000005510-0-tps-676-326.jpg"
            alt=""
          />
        </div>
        <Divider></Divider>
        <Button type="primary" onClick={sendTopCard} block>
          试一试
        </Button>
      </Card>
    </div>
  );
};
const container = document.getElementById('root');
ReactDOM.render(<Home />, container);
